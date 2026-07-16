# Constraint-Driven Architecture

A Codex skill for turning product constraints into the simplest defensible software architecture.

It is for greenfield architecture, major system evolution, and explicit architecture reviews. It resists unjustified microservices, infrastructure, and technology choices.

## Install for Codex

### Windows PowerShell

```powershell
git clone https://github.com/Liparakis/constraint-driven-architecture.git "$env:USERPROFILE\.agents\skills\constraint-driven-architecture"
```

### macOS or Linux

```bash
git clone https://github.com/Liparakis/constraint-driven-architecture.git ~/.agents/skills/constraint-driven-architecture
```

Start a new Codex session if the skill does not appear automatically.

## Use

```text
/constraint-driven-architecture Design the initial architecture for a local-first collaborative agent platform using modern Java 25 where appropriate.
```

The skill can also activate when a request clearly calls for architecture work.

## Files

- `SKILL.md` - core workflow
- `references/` - evidence, output, and architecture gates
- `agents/openai.yaml` - Codex UI metadata
