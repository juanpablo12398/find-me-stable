import { test, expect } from '@playwright/test';

function requiredEnv(name) {
  const value = process.env[name];
  if (!value) throw new Error(`Missing env var: ${name}`);
  return value;
}

const E2E_USER_DNI = requiredEnv('E2E_USER_DNI');
const E2E_USER_EMAIL = requiredEnv('E2E_USER_EMAIL');

test('Login (DNI + Email) desde el header', async ({ page }) => {
  await page.goto('/');

  const loginLink = page.getByRole('link', { name: /iniciar sesi[o\u00f3]n/i });
  await expect(loginLink).toBeVisible();
  await loginLink.click();

  const dni = page.getByPlaceholder(/ingresa tu dni/i);
  const email = page.getByPlaceholder(/tu@email\.com/i);

  await expect(dni).toBeVisible();
  await dni.fill(E2E_USER_DNI);
  await email.fill(E2E_USER_EMAIL);

  const form = page.locator('form');
  const submit = form.getByRole('button', { name: /^iniciar sesi[o\u00f3]n$/i });

  await expect(submit).toBeVisible();
  await expect(submit).toBeEnabled();
  await submit.click();

  await expect(page).not.toHaveURL(/iniciar|login/i);
});