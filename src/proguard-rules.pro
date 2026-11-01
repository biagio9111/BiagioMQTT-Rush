# Prevents extension classes (annotated with `@Extension`) from being removed, renamed or repackaged.
-keep @com.google.appinventor.components.annotations.Extension public class * {
  public *;
}

# Prevents helper classes from being removed, renamed or repackaged
-keepnames class * implements com.google.appinventor.components.common.OptionList {
  *;
}

# App Inventor ships an old androidx.annotation. Libraries built against newer
# versions reference annotations it lacks, which are only needed to compile.
-dontwarn androidx.annotation.**
