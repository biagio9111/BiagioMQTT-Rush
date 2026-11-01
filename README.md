# BiagioMQTT

An App Inventor 2 extension created using [Rush](https://github.com/shreyashsaitwal/rush-cli).

## Building

Run `rush build` in this directory. The extension is written to the `out`
directory as an `.aix` file, ready to import into App Inventor.

Run `rush build --optimize` to also optimize and obfuscate the extension's code.

## Releasing

GitHub Actions builds the extension when a push or pull request changes it. To publish
a release, push a tag named after the version in `rush.yml`, like `v1.0.0`.
The workflow attaches the `.aix` file and its documentation to the release.
