const { test, expect } = require('@playwright/test');

test.describe('US-09 — Navigation & Layout', () => {
  // T-SYS.23
  test('T-SYS.23 header shows Spring Music branding', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('.navbar-brand')).toContainText('Spring Music');
    await expect(page.locator('.navbar-brand .glyphicon-music')).toBeVisible();
  });

  // T-SYS.3
  test('T-SYS.3 info dropdown shows profiles and services', async ({ page }) => {
    await page.goto('/');
    await page.click('.glyphicon-info-sign');
    await expect(page.locator('.dropdown-menu:has-text("Profiles")')).toBeVisible();
    await expect(page.locator('.dropdown-menu:has-text("Services")')).toBeVisible();
  });

  // T-SYS.9
  test('T-SYS.9 errors page shows Kill and Throw buttons', async ({ page }) => {
    await page.goto('/#/errors');
    await expect(page.locator('h1')).toHaveText('Force Errors');
    await expect(page.locator('a.btn:has-text("Kill")')).toBeVisible();
    await expect(page.locator('a.btn:has-text("Throw Exception")')).toBeVisible();
  });

  // T-SYS.22
  test('T-SYS.22 unknown route defaults to albums view', async ({ page }) => {
    await page.goto('/#/nonexistent');
    await page.waitForSelector('.thumbnail, table.table');
    await expect(page.locator('[ng-repeat*="album in albums"]')).toHaveCount(29);
  });
});

test.describe('US-08 — Status Messages', () => {
  // T-SYS.24
  test('T-SYS.24 success message shown after save', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.thumbnail');

    await page.click('a:has-text("add an album")');
    await page.fill('#title', 'StatusTest');
    await page.fill('#artist', 'Test');
    await page.fill('#releaseYear', '2020');
    await page.fill('#genre', 'Rock');
    await page.click('.modal-footer button.btn-primary');

    await expect(page.locator('.alert-success')).toBeVisible();
    await expect(page.locator('.alert-success')).toContainText('Album saved');
  });

  // T-SYS.26
  test('T-SYS.26 dismiss message clears alert', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.thumbnail');

    await page.click('a:has-text("add an album")');
    await page.fill('#title', 'DismissTest');
    await page.fill('#artist', 'Test');
    await page.fill('#releaseYear', '2020');
    await page.fill('#genre', 'Rock');
    await page.click('.modal-footer button.btn-primary');

    await page.waitForSelector('.alert-success');
    await page.click('.alert .glyphicon-remove-circle');
    await expect(page.locator('.alert')).not.toBeVisible();
  });
});
