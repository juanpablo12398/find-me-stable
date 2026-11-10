import { test, expect } from '@playwright/test';

test('Lista de desaparecidos: carga cards, filtra y pagina con dataset seed', async ({ page }) => {
  await page.goto('/ListaDesaparecidos');

  await expect(page.getByRole('heading', { name: /personas extraviadas/i })).toBeVisible();

  const searchInput = page.getByPlaceholder(/buscar por nombre, dni o descripcion/i);
  await searchInput.fill('PaginacionE2E');
  await page.waitForTimeout(700);

  await expect(page.getByRole('button', { name: /ver en mapa/i }).first()).toBeVisible();
  await expect(page.getByText(/mostrando/i)).toContainText(/de\s+15/i);

  const nextButton = page.getByRole('button', { name: /^siguiente$/i });
  await expect(nextButton).toBeEnabled();
  await nextButton.click();

  await expect(page.getByText(/pagina 2/i).first()).toBeVisible();

  const prevButton = page.getByRole('button', { name: /^anterior$/i });
  await expect(prevButton).toBeEnabled();
});
