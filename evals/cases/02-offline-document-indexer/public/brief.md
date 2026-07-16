# Offline Document Indexer

Design a private Java desktop application for one user. It indexes text, PDF, and DOCX files on local disks and supports search without a network connection. The files are the source of truth: users can add, delete, rename, or modify them outside the application. The index must reconcile those changes and recover after a crash without silently claiming that a file was indexed when it was not.

Files can be large or malformed. Indexing should make bounded progress, report per-file failures, and avoid unbounded memory use. The documents are private. Explain which controls are application behavior, filesystem permissions, or OS/process boundaries; do not claim that a parser is a security sandbox unless the design actually creates one. There is no server, account system, or shared remote data requirement.
