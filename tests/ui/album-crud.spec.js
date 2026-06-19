const { test, expect } = require('@playwright/test');

test.describe('US-03 — Add an Album', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.thumbnail');
  });

  // T-03.1
  test('T-03.1 add album opens modal with empty fields', async ({ page }) => {
    await page.click('a:has-text("add an album")');
    await expect(page.locator('.modal-header h3')).toHaveText('Add an album');
    await expect(page.locator('#title')).toHaveValue('');
    await expect(page.locator('#artist')).toHaveValue('');
    await expect(page.locator('#releaseYear')).toHaveValue('');
    await expect(page.locator('#genre')).toHaveValue('');
  });

  // T-03.2
  test('T-03.2 OK button disabled when fields empty', async ({ page }) => {
    await page.click('a:has-text("add an album")');
    const okBtn = page.locator('.modal-footer button.btn-primary');
    await expect(okBtn).toBeDisabled();
  });

  // T-03.7 + T-03.8
  test('T-03.7/T-03.8 year validation accepts 2020, rejects abc', async ({ page }) => {
    await page.click('a:has-text("add an album")');
    await page.fill('#title', 'Test');
    await page.fill('#artist', 'Test');
    await page.fill('#genre', 'Rock');

    await page.fill('#releaseYear', '2020');
    const okBtn = page.locator('.modal-footer button.btn-primary');
    await expect(okBtn).toBeEnabled();

    await page.fill('#releaseYear', 'abc');
    await expect(okBtn).toBeDisabled();
  });

  // T-03.12
  test('T-03.12 submit valid album adds to catalog', async ({ page }) => {
    const beforeCount = await page.locator('[ng-repeat*="album in albums"]').count();

    await page.click('a:has-text("add an album")');
    await page.fill('#title', 'Playwright Test Album');
    await page.fill('#artist', 'Auto Tester');
    await page.fill('#releaseYear', '2024');
    await page.fill('#genre', 'Electronic');
    await page.click('.modal-footer button.btn-primary');

    await page.waitForTimeout(1000);
    const afterCount = await page.locator('[ng-repeat*="album in albums"]').count();
    expect(afterCount).toBe(beforeCount + 1);
  });

  // T-03.14
  test('T-03.14 cancel closes modal without adding', async ({ page }) => {
    const beforeCount = await page.locator('[ng-repeat*="album in albums"]').count();

    await page.click('a:has-text("add an album")');
    await page.fill('#title', 'Should Not Exist');
    await page.click('.modal-footer button.btn-warning');

    await page.waitForTimeout(500);
    const afterCount = await page.locator('[ng-repeat*="album in albums"]').count();
    expect(afterCount).toBe(beforeCount);
  });
});

test.describe('US-04 — Edit an Album', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.thumbnail');
  });

  // T-04.5
  test('T-04.5 inline edit activates on field click', async ({ page }) => {
    const firstTitle = page.locator('.thumbnail .caption h4 span[ng-transclude]').first();
    await firstTitle.click();
    await expect(page.locator('.thumbnail .caption h4 input')).toBeVisible();
  });

  // T-04.8
  test('T-04.8 inline edit cancel via Esc reverts', async ({ page }) => {
    const firstTitle = page.locator('.thumbnail .caption h4 span[ng-transclude]').first();
    const originalText = await firstTitle.textContent();
    await firstTitle.click();
    await page.keyboard.type('CHANGED');
    await page.keyboard.press('Escape');
    await page.waitForTimeout(500);
    const afterText = await page.locator('.thumbnail .caption h4 span[ng-transclude]').first().textContent();
    expect(afterText.trim()).toBe(originalText.trim());
  });
});

test.describe('US-05 — Delete an Album', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.thumbnail');
  });

  // T-05.1
  test('T-05.1 delete removes album from catalog', async ({ page }) => {
    // First add a test album to delete
    await page.click('a:has-text("add an album")');
    await page.fill('#title', 'ToDelete');
    await page.fill('#artist', 'Temp');
    await page.fill('#releaseYear', '2020');
    await page.fill('#genre', 'Rock');
    await page.click('.modal-footer button.btn-primary');
    await page.waitForTimeout(1000);

    const countBefore = await page.locator('[ng-repeat*="album in albums"]').count();

    // Find and delete the last album (our test album)
    const lastGear = page.locator('.dropdown-toggle').last();
    await lastGear.click();
    await page.click('a:has-text("delete")');
    await page.waitForTimeout(1000);

    const countAfter = await page.locator('[ng-repeat*="album in albums"]').count();
    expect(countAfter).toBe(countBefore - 1);
  });
});
