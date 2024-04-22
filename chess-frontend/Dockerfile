# Base image
FROM oven/bun:latest

# Set the working directory in the container
WORKDIR /app

# Copy package.json and bun.lockb
COPY package.json .
COPY bun.lockb .

# Install dependencies
RUN bun install

# Copy the source code
COPY src .

# Set the environment to development
ENV NODE_ENV=development

# Expose the port the application will run on
EXPOSE 3000

# Start the BUN server in development mode
CMD ["bun", "run", "dev"]