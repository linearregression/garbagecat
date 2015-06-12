Garbage Cat parses Java garbage collection logging and provides analysis to support JVM tuning and troubleshooting for OpenJDK and Sun JDK. It differs from other tools in that it goes beyond the simple math of calculating statistics such as maximum pause time and throughput. It adds context to these numbers by identifying the associated collector or collector phase, which allows for much deeper insight and analysis. This is especially relevant to collectors such as the Concurrent Mark Sweep collector that have multiple concurrent and stop-the-world phases.

Currrently a command line tool, an Eclipse RCP version is in development.

[Documentation](Documentation.md)