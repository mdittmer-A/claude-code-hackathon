const { test, expect } = require('@playwright/test');

test.describe('US-01 — View the Catalog', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.thumbnail, table.table');
  });

  // T-01.1
  test('T-01.1 all 29 albums visible on load', async ({ page }) => {
    const albums = page.locator('[ng-repeat*="album in albums"]');
    await expect(albums).toHaveCount(29);
  });

  // T-01.2
  test('T-01.2 album cards show title, artist, year, genre', async ({ page }) => {
    const firstCard = page.locator('.thumbnail .caption').first();
    await expect(firstCard.locator('h4').first()).not.toBeEmpty();
    await expect(firstCard.locator('h5').first()).not.toBeEmpty();
  });

  // T-01.5
  test('T-01.5 sort by title reorders albums', async ({ page }) => {
    await page.click('a:has-text("title")');
    const firstTitle = await page.locator('[ng-repeat*="album in albums"] h4').first().textContent();
    expect(firstTitle.trim().length).toBeGreaterThan(0);
  });

  // T-01.9
  test('T-01.9 toggle sort direction changes chevron', async ({ page }) => {
    const chevron = page.locator('.glyphicon-chevron-up, .glyphicon-chevron-down');
    const classBefore = await chevron.getAttribute('class');
    await chevron.click();
    const classAfter = await chevron.getAttribute('class');
    expect(classBefore).not.toBe(classAfter);
  });
});

test.describe('US-02 — Switch Display Layout', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.thumbnail');
  });

  // T-02.1
  test('T-02.1 default view is grid', async ({ page }) => {
    await expect(page.locator('.thumbnail')).toHaveCount(29);
  });

  // T-02.2
  test('T-02.2 switch to list view', async ({ page }) => {
    await page.click('.glyphicon-th-list');
    await expect(page.locator('table.table')).toBeVisible();
    await expect(page.locator('table.table tbody tr')).toHaveCount(29);
  });

  // T-02.3
  test('T-02.3 switch back to grid view', async ({ page }) => {
    await page.click('.glyphicon-th-list');
    await page.click('.glyphicon-th');
    await expect(page.locator('.thumbnail')).toHaveCount(29);
  });

  // T-02.5
  test('T-02.5 view preference not persisted after refresh', async ({ page }) => {
    await page.click('.glyphicon-th-list');
    await page.reload();
    await page.waitForSelector('.thumbnail');
    await expect(page.locator('.thumbnail')).toHaveCount(29);
  });
});
