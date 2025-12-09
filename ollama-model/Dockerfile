FROM ollama/ollama

EXPOSE 11434
VOLUME /root/.ollama

RUN printf '%s\n' \
    "#!/bin/bash" \
    "set -e" \
    "" \
    "# Start Ollama in the background" \
    "ollama serve &" \
    "pid=\$!" \
    "" \
    "echo \"Waiting for Ollama to become ready...\"" \
    "until curl -s http://localhost:11434/api/tags > /dev/null 2>&1; do" \
    "    sleep 1" \
    "done" \
    "" \
    "# Pull model ONLY on first run" \
    "if ! ollama list | grep -q \"llama3.1\"; then" \
    "    echo \"Pulling llama3.1...\"" \
    "    ollama pull llama3.1" \
    "fi" \
    "" \
    "wait \$pid" \
    > /usr/local/bin/entrypoint.sh && chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
