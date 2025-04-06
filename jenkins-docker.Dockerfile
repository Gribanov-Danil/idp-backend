FROM jenkins/jenkins:lts

USER root

# Установка Docker CLI
RUN apt-get update && \
    apt-get install -y docker.io && \
    usermod -aG docker jenkins

# По желанию: установить node/npm глобально, если хочешь запускать без docker-agent
# RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
#     apt-get install -y nodejs

USER jenkins
