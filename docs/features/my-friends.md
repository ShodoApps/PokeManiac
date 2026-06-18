# Feature: My Friends

**Status:** Live
**Last updated:** 2026-06-19

---

## Purpose
Allows users to browse the list of friends they are subscribed to and view each friend's transaction history. Users manage their social graph here — they can see who they follow and unsubscribe from friends they no longer want to track.

---

## User Flows

### Flow 1: Browse Friend List
1. User opens My Friends screen
2. Loading state shown while friends are fetched
3. List of subscribed friends is displayed — each entry shows: avatar, name
4. If the user has no subscriptions, an empty state is shown with a CTA to search for friends

### Flow 2: View a Friend's Profile
1. User taps a friend in the list
2. Navigates to Friend Detail screen
3. Loading state shown while friend's data and transaction history are fetched
4. Friend's profile is shown: avatar, name, transaction history (list of cards they've traded)

### Flow 3: Unsubscribe from the List Screen
1. User taps "Unsubscribe" on a friend in the list
2. Friend is removed from the list immediately
3. API call is made in background; on failure, error snackbar shown

### Flow 4: Unsubscribe from Friend Detail
1. User is on the Friend Detail screen
2. User taps "Unsubscribe"
3. API call is made; on success, user is navigated back to the Friend List
4. The unsubscribed friend is no longer in the list

---

## Screens

| Screen | What it shows | Entry point |
|---|---|---|
| MyFriendListScreen | List of subscribed friends + unsubscribe button per row | Main navigation |
| MyFriendDetailScreen | Friend's profile: avatar, name, transaction history + unsubscribe button | Tap a friend in the list |

---

## States

### MyFriendListScreen
| State | When | What the user sees |
|---|---|---|
| Loading | Initial load | Full-screen spinner |
| Data | Friends found | Scrollable list of friend cards |
| Empty | No subscriptions | Empty state + "Find Friends" button |

### MyFriendDetailScreen
| State | When | What the user sees |
|---|---|---|
| Loading | Navigating to detail | Full-screen spinner |
| Data | Friend data loaded | Profile header + transaction list |

---

## Business Rules
- The friend list shows only users the current user has subscribed to
- Unsubscribing from the list screen removes the friend from the list immediately (reactive Room query)
- Unsubscribing from the detail screen triggers a back navigation when the Room query emits `null` for the friend (the friend no longer exists in the subscriptions table)
- Friend transaction history shows the same card format as the Dashboard feed

---

## Error States
- **Friend list fails to load:** Stays on Loading state + error snackbar
- **Unsubscribe fails (list):** Error snackbar; list state is not reverted (Room query is the source of truth)
- **Unsubscribe fails (detail):** Error snackbar; user remains on detail screen
- **Friend detail fails:** Error snackbar; Detail shows Loading state

---

## Edge Cases
- Navigating to a friend's detail who has been unsubscribed from another screen: the detail ScreenModel observes the Room query; when it emits `null`, `unsubscribed` event fires and triggers back navigation
- Friend with no transactions: empty transaction section shown, not an error
- Very long name: truncated in list, shown fully in detail header

---

## Out of Scope
- Sending messages to friends
- Viewing a friend's full profile (photos, bio)
- Mutual friend lists ("friends of friends")
- Sorting or filtering the friend list
