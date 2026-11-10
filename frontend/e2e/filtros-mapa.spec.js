import { test, expect } from '@playwright/test';

test('Mapa: filtro por radio y por persona', async ({ page }) => {
  await page.goto('/MapaAvistamientos');

  await page.getByRole('button', { name: /por radio/i }).click();

  const map = page.locator('.leaflet-container').first();
  await expect(map).toBeVisible();

  const box = await map.boundingBox();
  if (!box) throw new Error('No pude obtener el bounding box del mapa');

  await page.mouse.click(box.x + box.width * 0.5, box.y + box.height * 0.5);

  await expect(page.getByText(/radio:\s*\d+\s*km/i)).toBeVisible();
  await expect(page.locator('.leaflet-overlay-pane path').first()).toBeVisible();

  await page.getByRole('button', { name: /por persona/i }).click();
  await expect(page.getByText(/seleccionar persona/i)).toBeVisible();
  await expect(page.getByRole('combobox').first()).toBeVisible();
});
