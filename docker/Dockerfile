FROM eclipse-temurin:21-jdk-noble

# Install Maven and GUI libraries
RUN apt-get update && apt-get install -y \
    maven \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libasound2t64 \
    libgtk-3-0 \
    libgl1 \
    libglx0 \
    libgl1-mesa-dri \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Ensure we have a place for maven settings and repository
RUN mkdir -p /app/.m2/repository && chmod -R 777 /app

# We will run as UID 1000 from compose directly
# No need for complex useradd if we use 'user: 1000' in compose

CMD ["mvn", "spring-boot:run", "-Dmaven.repo.local=/app/.m2/repository", "-Duser.home=/app"]
