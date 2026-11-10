import { test, expect } from '@playwright/test';

function requiredEnv(name) {
  const value = process.env[name];
  if (!value) throw new Error(`Missing env var: ${name}`);
  return value;
}

const E2E_USER_DNI = requiredEnv('E2E_USER_DNI');
const E2E_USER_EMAIL = requiredEnv('E2E_USER_EMAIL');

test('Reporte de avistamiento desde mapa (login + reportar + modal)', async ({ page }) => {
  await page.goto('/');

  await page.getByRole('link', { name: /iniciar sesi[o\u00f3]n/i }).first().click();

  await page.getByPlaceholder(/ingresa tu dni/i).fill(E2E_USER_DNI);
  await page.getByPlaceholder(/tu@email\.com/i).fill(E2E_USER_EMAIL);

  const form = page.locator('form').first();
  const submitLogin = form.getByRole('button', { name: /^iniciar sesi[o\u00f3]n$/i });

  await submitLogin.scrollIntoViewIfNeeded();
  await expect(submitLogin).toBeEnabled();
  await submitLogin.click();

  await expect(page).not.toHaveURL(/iniciar|login/i);

  const reportarBtn = page.getByRole('button', { name: /activar modo reporte/i });
  await expect(reportarBtn).toBeVisible();
  await reportarBtn.click({ force: true });

  const map = page.locator('.leaflet-container').first();
  await expect(map).toBeVisible();

  const box = await map.boundingBox();
  if (!box) throw new Error('No pude obtener el bounding box del mapa');

  await page.mouse.click(box.x + box.width * 0.55, box.y + box.height * 0.55);

  const modalTitle = page.getByRole('heading', { name: /reportar avistamiento/i });
  await expect(modalTitle).toBeVisible();

  const selectCombobox = page.getByRole('combobox').first();
  if (await selectCombobox.count()) {
    await selectCombobox.click();
  } else {
    await page.getByText('Seleccionar persona...').first().click();
  }

  const firstOption = page.getByRole('option').first();
  await expect(firstOption).toBeVisible();
  await firstOption.click();

  const desc = page.getByPlaceholder(/describe las circunstancias del avistamiento/i);
  await expect(desc).toBeVisible();
  await desc.fill('Lo vi caminando cerca del parque ayer por la tarde.');

  const enviar = page.getByRole('button', { name: /enviar reporte/i });
  await enviar.scrollIntoViewIfNeeded();
  await expect(enviar).toBeEnabled();
  await enviar.click();
});
