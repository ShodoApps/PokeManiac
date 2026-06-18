# ADR-009: Offline / Cache Strategy

**Status:** Accepted
**Date:** 2026-06-18

## Context

PokeManiac is a social app used on mobile devices with unreliable connectivity. Users expect to see content even when offline or on a slow network. Without a cache, every lost connection results in an empty screen.

We needed to decide:
- Where is data cached (memory, disk, or both)?
- When is the cache refreshed (on demand, on a schedule, always-on)?
- What do screens show while the network fetch is in progress?

## Decision

We chose **Room as the single local cache**, accessed through the repository pattern.

Each repository implementation follows this pattern:
- Local data (Room) is the primary source for screen display — emitted immediately from a `Flow<>` query
- When `start()` is called, a fresh network fetch is triggered in parallel
- When the network response arrives, the repository writes it to Room
- Room's reactive query (`Flow<>`) automatically reemits the updated data to the screen
- The screen transitions from cached data → fresh data without explicit loading states for refreshes

This is sometimes called "stale-while-revalidate".

For write operations (posting a transaction, adding a friend), we write to the remote API first; on success, we update the local Room cache. No optimistic local writes.

Cache invalidation is not yet formalised — the cache is currently treated as permanent until overwritten by a fresh network fetch.

## Consequences

**Easier:**
- Users see content immediately on screen open, even on slow connections
- Empty states are rare — cached data is almost always available after the first load
- The repository handles the merge logic; the ScreenModel never knows whether data came from cache or network

**Harder:**
- Cache invalidation is not formalised — stale data can persist if the network fetch fails or is skipped
- The two-source (local + remote) flow logic in `RepositoryImpl` is slightly more complex than a direct network call
- Testing requires mocking both `*Request` and `*DataStore` and asserting the correct merge behaviour

**Common mistake to avoid:** Showing a `Loading` state while the Room query is active. Room emits immediately (even if the table is empty). Only show `Loading` before the first Room emission; subsequent refreshes should update data silently.
