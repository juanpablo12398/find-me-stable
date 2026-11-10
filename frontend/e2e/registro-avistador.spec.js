import { test, expect } from '@playwright/test';

const REGISTER_DNI = process.env.E2E_REGISTER_DNI || '35123456';
const REGISTER_NAME = process.env.E2E_REGISTER_NAME || 'Carlos';
const REGISTER_LASTNAME = process.env.E2E_REGISTER_LASTNAME || 'Vazquez';
const REGISTER_AGE = process.env.E2E_REGISTER_AGE || '30';
const REGISTER_ADDRESS = process.env.E2E_REGISTER_ADDRESS || 'Calle E2E Registro 123';
const REGISTER_PHONE = process.env.E2E_REGISTER_PHONE || '1133344455';
const REGISTER_EMAIL =
  process.env.E2E_REGISTER_EMAIL || `registro.${Date.now()}@findme.test`;

test('Registro de avistador redirige al mapa y muestra badge de usuario', async ({ page }) => {
  await page.goto('/Registro');

  await page.locator('input[name="nombre"]').fill(REGISTER_NAME);
  await page.locator('input[name="apellido"]').fill(REGISTER_LASTNAME);
  await page.locator('input[name="dni"]').fill(REGISTER_DNI);
  await page.locator('input[name="edad"]').fill(REGISTER_AGE);
  await page.locator('input[name="direccion"]').fill(REGISTER_ADDRESS);
  await page.locator('input[name="email"]').fill(REGISTER_EMAIL);
  await page.locator('input[name="telefono"]').fill(REGISTER_PHONE);

  await page.locator('main').getByRole('button', { name: 'Registrarme', exact: true }).click();

  await expect(page).toHaveURL(/\/MapaAvistamientos/i);
  await expect(page.locator('header')).toContainText(new RegExp(`${REGISTER_NAME}|${REGISTER_DNI}`, 'i'));
});
