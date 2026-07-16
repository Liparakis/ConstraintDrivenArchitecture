# Batch File Converter CLI

Design a command-line application that converts large local files in batches on one machine. It must resume after interruption, report failures per input, avoid corrupting an output, and make reruns predictable. The operator starts it manually; there are no users, accounts, network calls, shared deployment, or background-service requirement.

Input and output files may be large, so the design must discuss streaming or bounded memory and a sensible concurrency limit. A single machine is the failure and scaling boundary for this version. Keep the design easy for one developer to operate and explain what measurement would justify changing it.
