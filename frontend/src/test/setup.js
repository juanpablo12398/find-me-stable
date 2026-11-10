import '@testing-library/jest-dom';
import { afterAll, beforeAll, vi } from 'vitest';

// JSDOM does not implement scrollTo; mock it once for all tests.
Object.defineProperty(window, 'scrollTo', {
  value: vi.fn(),
  writable: true,
});

const originalWarn = console.warn;
let warnSpy;

beforeAll(() => {
  warnSpy = vi.spyOn(console, 'warn').mockImplementation((...args) => {
    const message = String(args[0] ?? '');
    if (message.includes('React Router Future Flag Warning')) {
      return;
    }
    originalWarn(...args);
  });
});

afterAll(() => {
  if (warnSpy) {
    warnSpy.mockRestore();
  }
});
