# Study Integrity Logger

Study Integrity Logger is a local JetBrains IDE plugin for recording a tamper-evident study workflow log.

The plugin is intended for students who want to keep a private technical record of their work process while completing programming assignments. It records metadata about IDE activity, file saves, project sessions, submission hashes, and evidence reports.

The plugin does **not** store source code contents and does **not** send data anywhere.

## Important limitation

This plugin does not mathematically prove that no external sharing was possible.

It provides a tamper-evident record of the observed IDE/project workflow:

- when the project was opened or closed;
- when files were opened, selected, or saved;
- what SHA-256 hash each file had at the time of the event;
- whether the event log hash chain is valid;
- what the final project tree hash was;
- what submission file hash was generated.

This evidence can support an explanation of independent work, but it should be used together with screen recording, Git history, allowed source notes, and the ability to explain the solution.

## Features

- Logs project events:
  - `PROJECT_OPENED`
  - `PROJECT_CLOSED`

- Logs file events:
  - `FILE_OPENED`
  - `FILE_SELECTED`
  - `FILE_SAVED`

- Logs plugin state changes:
  - `PLUGIN_ENABLED`
  - `PLUGIN_DISABLED`

- Creates a hash-chained event log:
  - `prev_hash`
  - `event_hash`

- Verifies event log integrity.

- Generates:
  - `verification-report.txt`
  - `session-info.json`
  - `session-final.json`
  - `submission-evidence.txt`

- Computes:
  - SHA-256 of files;
  - SHA-256 of selected submission file;
  - project tree hash;
  - final log hash;
  - Git HEAD commit, if the project is a Git repository.

- Exports an evidence package ZIP without source code.

- Provides:
  - Tools menu actions;
  - status bar ON/OFF widget;
  - persistent ON/OFF state.

## Generated files

The plugin creates a local `.study-log` directory inside the project:

```text
.study-log/
├── file-events.log
├── verification-report.txt
├── session-info.json
├── session-final.json
├── submission-evidence.txt
├── README-evidence.txt
└── study-evidence-YYYY-MM-DD-HHMMSS.zip
