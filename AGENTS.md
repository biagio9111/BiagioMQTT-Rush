# Agent instructions

This is an MIT App Inventor extension built with Rush.

Before changing the extension, read `.claude/skills/rush/SKILL.md`.
It explains how blocks, dropdowns, events, dependencies and resources work,
and the limits App Inventor imposes.

## Workflow

1. Run `rush build` after every change and fix every error it reports.
2. Read `out/<package>.md` to check that the blocks match what was asked.
3. Tell the user what to test in App Inventor: `rush build` can't run the
   extension.

## Rules

- Blocks are public methods of the `@Extension` class, annotated with
  `@SimpleFunction`, `@SimpleProperty` or `@SimpleEvent`. Use primitive
  types.
- Extensions are non-visible. To show a view, add it to an arrangement passed
  to a block.
- Dropdown enums go in a directory named `helpers`.
- Fire events on the main thread, never while `Screen.Initialize` runs.
- Declare every library in `rush.yml`.
- Don't edit `.rush/` or `out/`: Rush generates them.

## Project notes

Add conventions specific to this project here.
