import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
    plugins: [react()],
    test: {
        environment: 'jsdom',
        globals: true,
        setupFiles: ['./src/test/setup.js'],
        coverage: {
            provider: 'v8',
            reporter: ['text', 'html', 'json-summary'],
            reportsDirectory: './coverage',
            include: ['src/**/*.{js,jsx}'],
            exclude: [
                'src/test/**',
                'src/main.jsx',
                'src/components/ui/**',
                '**/*.d.ts',
                'dist/**',
                'node_modules/**'
            ],
            thresholds: {
                statements: 15,
                branches: 15,
                functions: 12,
                lines: 15
            }
        }
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
        },
    },
});
