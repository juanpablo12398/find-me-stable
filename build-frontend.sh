#!/usr/bin/env bash

set -e

echo "Building frontend..."

cd frontend
npm install

# Production build
npm run build

# Copy build to backend static resources
echo "Copying build to backend static resources..."
cd ..
cp -r frontend/dist/* src/main/resources/static/

echo "Frontend build complete!"