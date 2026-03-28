import { test, expect } from '@playwright/test';

test('has title', async ({ page }) => {
  await page.goto('http://localhost:8080');
  await expect(page).toHaveTitle(/Hello/);
});

test('Login, get enterprise, get vehicle, edit brand, check', async ({ page }) => {

    await page.goto('http://localhost:8080/login');
    await page.fill('#username', 'man1');
    await page.fill('#password', '1111');
    //await page.click("button[type='submit']");
    await Promise.all([
        page.waitForURL('**/enterprises'),
        page.click('button[type="submit"]')
    ]);

    const listItems = page.locator('.enterprise-row');
    await expect(listItems.first()).toBeVisible({ timeout: 5000 });
    const enterpriseRow = page.locator('#enterprisesTable .enterprise-row');
    const targetEnterprise = enterpriseRow.filter({ hasText: 'Главное' });
    await targetEnterprise.click();

    await expect(page.locator('#paginationInfo')).not.toContainText('0 из 0');
    const carRow = page.locator('#carsTableBody tr').filter({ hasText: 'A100AA100' });
    await expect(carRow).toBeVisible();
    await carRow.locator('button.edit-car-btn').click();

    const modal = page.locator('#carModal');
    await expect(modal).toBeVisible();

    const brandSelect = page.locator('select#brand');
    const curBrandId = await brandSelect.inputValue();
    const newBrandId = curBrandId === '1' ? '2' : '1';

    await brandSelect.selectOption(newBrandId);
    const expectedName = await brandSelect.locator(`option[value="${newBrandId}"]`).textContent();

    const requestPromise = page.waitForRequest(request =>
      request.url().includes('/api/vehicles/') && request.method() === 'PUT'
    );
    const responsePromise = page.waitForResponse(response =>
      response.url().includes('/api/vehicles/') &&
      response.request().method() === 'PUT'
    );

    await page.click('#saveCarBtn');

    const request = await requestPromise;
    const postData = JSON.parse(request.postData() || '{}');
    expect(postData.brandId).toBe(newBrandId);

    const response = await responsePromise;
    expect(response.status()).toBe(200);

    await expect(modal).toBeHidden();

    const updatedCarRow = page.locator('#carsTableBody tr').filter({ hasText: 'A100AA100' });
    await expect(updatedCarRow.locator('td').first()).toHaveText(expectedName?.trim() || '');
  });