const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

describe('System Endpoints — Phase 0 Characterization', () => {
  // T-SYS.1: App info
  test('T-SYS.1 GET /appinfo returns profiles and services', async () => {
    const res = await fetch(`${BASE_URL}/appinfo`);
    expect(res.status).toBe(200);
    const info = await res.json();
    expect(info).toHaveProperty('profiles');
    expect(info).toHaveProperty('services');
    expect(Array.isArray(info.profiles)).toBe(true);
    expect(Array.isArray(info.services)).toBe(true);
  });

  // T-SYS.2: Service info
  test('T-SYS.2 GET /service returns array', async () => {
    const res = await fetch(`${BASE_URL}/service`);
    expect(res.status).toBe(200);
    const services = await res.json();
    expect(Array.isArray(services)).toBe(true);
  });

  // T-SYS.4: Health endpoint
  test('T-SYS.4 GET /actuator/health returns UP', async () => {
    const res = await fetch(`${BASE_URL}/actuator/health`);
    expect(res.status).toBe(200);
    const health = await res.json();
    expect(health.status).toBe('UP');
  });

  // T-SYS.5: Actuator discovery
  test('T-SYS.5 GET /actuator lists endpoints', async () => {
    const res = await fetch(`${BASE_URL}/actuator`);
    expect(res.status).toBe(200);
    const body = await res.json();
    expect(body).toHaveProperty('_links');
  });

  // T-SYS.20: Static index served
  test('T-SYS.20 GET / serves index.html', async () => {
    const res = await fetch(`${BASE_URL}/`);
    expect(res.status).toBe(200);
    const html = await res.text();
    expect(html).toContain('Spring Music');
    expect(html).toContain('ng-app="SpringMusic"');
  });

  // T-SYS.21: Static assets
  test('T-SYS.21 static CSS served', async () => {
    const res = await fetch(`${BASE_URL}/css/app.css`);
    expect(res.status).toBe(200);
  });

  // T-SYS.7: Throw endpoint returns 500
  test('T-SYS.7 GET /errors/throw returns 500', async () => {
    const res = await fetch(`${BASE_URL}/errors/throw`);
    expect(res.status).toBe(500);
  });

  // T-SYS.11: No re-seeding (count stays 29 after re-fetch)
  test('T-SYS.11 album count remains stable (no duplicates)', async () => {
    const res1 = await fetch(`${BASE_URL}/albums`);
    const count1 = (await res1.json()).length;

    // Fetch again
    const res2 = await fetch(`${BASE_URL}/albums`);
    const count2 = (await res2.json()).length;

    expect(count1).toBe(count2);
  });
});
