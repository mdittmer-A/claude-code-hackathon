const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

describe('Album CRUD API — Phase 0 Characterization', () => {
  // T-01.1 / T-SYS.10: List all albums (seed data)
  test('T-01.1 GET /albums returns 29 albums on fresh start', async () => {
    const res = await fetch(`${BASE_URL}/albums`);
    expect(res.status).toBe(200);
    const albums = await res.json();
    expect(albums).toHaveLength(29);
  });

  // T-01.2: Each album has required fields
  test('T-01.2 albums have correct fields', async () => {
    const res = await fetch(`${BASE_URL}/albums`);
    const albums = await res.json();
    for (const album of albums) {
      expect(album).toHaveProperty('id');
      expect(album).toHaveProperty('title');
      expect(album).toHaveProperty('artist');
      expect(album).toHaveProperty('releaseYear');
      expect(album).toHaveProperty('genre');
      expect(album).toHaveProperty('trackCount');
    }
  });

  // T-SYS.12: Spot-check seed data
  test('T-SYS.12 seed data contains expected albums', async () => {
    const res = await fetch(`${BASE_URL}/albums`);
    const albums = await res.json();
    const titles = albums.map(a => a.title);
    expect(titles).toContain('Nevermind');
    expect(titles).toContain('Thriller');
    expect(titles).toContain("Singin' The Blues");
    expect(titles).toContain('IV');
  });

  // T-SYS.12: Genre distribution
  test('T-SYS.12 genre counts match seed data', async () => {
    const res = await fetch(`${BASE_URL}/albums`);
    const albums = await res.json();
    const rock = albums.filter(a => a.genre === 'Rock');
    const pop = albums.filter(a => a.genre === 'Pop');
    const blues = albums.filter(a => a.genre === 'Blues');
    expect(rock).toHaveLength(22);
    expect(pop).toHaveLength(1);
    expect(blues).toHaveLength(6);
  });

  // T-03.16: Album IDs are strings, max 40 chars
  test('T-03.16 album IDs are strings max 40 chars', async () => {
    const res = await fetch(`${BASE_URL}/albums`);
    const albums = await res.json();
    for (const album of albums) {
      expect(typeof album.id).toBe('string');
      expect(album.id.length).toBeLessThanOrEqual(40);
    }
  });

  // T-1.2: Get album by ID
  test('T-01.2 GET /albums/{id} returns single album', async () => {
    const listRes = await fetch(`${BASE_URL}/albums`);
    const albums = await listRes.json();
    const first = albums[0];

    const res = await fetch(`${BASE_URL}/albums/${first.id}`);
    expect(res.status).toBe(200);
    const album = await res.json();
    expect(album.id).toBe(first.id);
    expect(album.title).toBe(first.title);
  });

  // T-1.3: Get non-existent album
  test('T-01.3 GET /albums/{bad-id} returns empty/null', async () => {
    const res = await fetch(`${BASE_URL}/albums/nonexistent-id-12345`);
    expect(res.status).toBe(200);
    const body = await res.text();
    expect(body === '' || body === 'null').toBe(true);
  });

  // T-03.12 + T-03.16: Add new album
  test('T-03.12 PUT /albums creates album with generated ID', async () => {
    const newAlbum = {
      title: 'Test Album',
      artist: 'Test Artist',
      releaseYear: '2020',
      genre: 'Rock'
    };
    const res = await fetch(`${BASE_URL}/albums`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newAlbum),
    });
    expect(res.status).toBe(200);
    const created = await res.json();
    expect(created.id).toBeDefined();
    expect(created.id.length).toBeGreaterThan(0);
    expect(created.id.length).toBeLessThanOrEqual(40);
    expect(created.title).toBe('Test Album');

    // Cleanup
    await fetch(`${BASE_URL}/albums/${created.id}`, { method: 'DELETE' });
  });

  // T-04.11: Update album via POST
  test('T-04.11 POST /albums updates existing album', async () => {
    // Create one first
    const res = await fetch(`${BASE_URL}/albums`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: 'Before', artist: 'A', releaseYear: '2000', genre: 'Pop' }),
    });
    const created = await res.json();

    // Update
    created.title = 'After';
    const updateRes = await fetch(`${BASE_URL}/albums`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(created),
    });
    expect(updateRes.status).toBe(200);
    const updated = await updateRes.json();
    expect(updated.title).toBe('After');
    expect(updated.id).toBe(created.id);

    // Cleanup
    await fetch(`${BASE_URL}/albums/${created.id}`, { method: 'DELETE' });
  });

  // T-05.3: Delete album
  test('T-05.3 DELETE /albums/{id} removes album', async () => {
    // Create one first
    const res = await fetch(`${BASE_URL}/albums`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: 'ToDelete', artist: 'X', releaseYear: '2020', genre: 'Rock' }),
    });
    const created = await res.json();

    const delRes = await fetch(`${BASE_URL}/albums/${created.id}`, { method: 'DELETE' });
    expect(delRes.status).toBe(200);

    // Verify gone
    const getRes = await fetch(`${BASE_URL}/albums/${created.id}`);
    const body = await getRes.text();
    expect(body === '' || body === 'null').toBe(true);
  });

  // T-05.4: Delete non-existent
  test('T-05.4 DELETE /albums/{bad-id} does not crash', async () => {
    const res = await fetch(`${BASE_URL}/albums/nonexistent-xyz`, { method: 'DELETE' });
    expect(res.status).toBeLessThan(500);
  });

  // T-03.15 / T-01.8: Validation — empty body returns 400
  test('T-03.15 PUT /albums with empty body returns 400', async () => {
    const res = await fetch(`${BASE_URL}/albums`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({}),
    });
    expect(res.status).toBe(400);
  });
});
