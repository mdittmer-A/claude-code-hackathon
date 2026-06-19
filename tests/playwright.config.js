const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './ui',
  timeout: 30000,
  use: {
    baseURL: process.env.BASE_URL || 'http://localhost:8080',
    headless: true,
  },
});
