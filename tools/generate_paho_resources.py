
import json
import urllib.request
import zipfile
import io
from pathlib import Path

VERSION = "1.2.5"
URL = (
    "https://repo.maven.apache.org/maven2/"
    "org/eclipse/paho/org.eclipse.paho.client.mqttv3/"
    f"{VERSION}/org.eclipse.paho.client.mqttv3-{VERSION}.jar"
)

PACKAGE = "org.eclipse.paho.client.mqttv3.internal.nls"
FOLDER = Path("src/org/eclipse/paho/client/mqttv3/internal/nls")
FOLDER.mkdir(parents=True, exist_ok=True)

with urllib.request.urlopen(URL, timeout=30) as response:
    jar_bytes = response.read()

def parse_properties(raw):
    text = raw.decode("ISO-8859-1")
    logical = []
    current = ""

    for line in text.splitlines():
        if current:
            current += line.lstrip()
        else:
            current = line

        if current.endswith("\\") and not current.endswith("\\\\"):
            current = current[:-1]
            continue

        logical.append(current)
        current = ""

    result = []
    for line in logical:
        line = line.strip()
        if not line or line.startswith(("#", "!")):
            continue

        pos = -1
        for separator in ("=", ":"):
            p = line.find(separator)
            if p >= 0 and (pos < 0 or p < pos):
                pos = p

        if pos < 0:
            parts = line.split(None, 1)
            key = parts[0]
            value = parts[1] if len(parts) > 1 else ""
        else:
            key = line[:pos].strip()
            value = line[pos + 1:].strip()

        result.append((key, value))
    return result

with zipfile.ZipFile(io.BytesIO(jar_bytes)) as jar:
    for name in ("logcat", "messages"):
        resource = (
            "org/eclipse/paho/client/mqttv3/internal/nls/"
            + name + ".properties"
        )
        entries = parse_properties(jar.read(resource))

        source = [
            f"package {PACKAGE};",
            "",
            "import java.util.ListResourceBundle;",
            "",
            f"public class {name} extends ListResourceBundle {{",
            "    @Override",
            "    protected Object[][] getContents() {",
            "        return new Object[][] {"
        ]

        for key, value in entries:
            source.append(
                "            {"
                + json.dumps(key, ensure_ascii=True)
                + ", "
                + json.dumps(value, ensure_ascii=True)
                + "},"
            )

        source.extend([
            "        };",
            "    }",
            "}"
        ])

        target = FOLDER / (name + ".java")
        target.write_text("\n".join(source) + "\n", encoding="utf-8")
        print("Generated:", target, "entries:", len(entries))
