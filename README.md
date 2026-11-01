# Rush extension template

Create an [MIT App Inventor](https://appinventor.mit.edu) extension without installing
anything. Everything happens on GitHub, in your browser.

1. **Create your repository.** Click **Use this template** → **Create a new repository**
   at the top of this page, give it a name, and click **Create repository**.
2. **Create the extension.** In your new repository, open the **Actions** tab, choose
   **Create extension**, and click **Run workflow**. Fill in:
   - **Name:** your extension's name, like `WeatherInfo`.
   - **Package:** a name only you would use, written backwards like a web address, such
     as `io.github.yourname`.
   - **Language:** Java or Kotlin.

   After about a minute, your repository contains the extension's project, and the
   **Build** workflow builds it. Its run page has the extension (`.aix`) to download.
3. **Start working.** Click **Code** → **Codespaces** → **Create codespace on main**. VS
   Code opens in your browser, with everything needed to build the extension. Describe
   what you want to GitHub Copilot, or run `rush build` in the terminal yourself.

The project is made with [Rush](https://github.com/shreyashsaitwal/rush-cli). Its
[guide](https://github.com/shreyashsaitwal/rush-cli/blob/main/docs/README.md) explains
how extensions work, building and releasing them, and working with AI agents.
