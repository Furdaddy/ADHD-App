# Next Step

A small, calm app for managing ADHD day to day. It is built around one idea: **you only ever need to know the next step.**

## What's in it

| Tab | What it does | Why it helps with ADHD |
| --- | --- | --- |
| **Today** | Shows one highlighted *next step*, plus a list capped at three tasks. Anything else goes to *Later*. Tasks can be split into tiny steps. | Cuts overwhelm and decision fatigue. Breaking tasks down gets you past the "wall" of starting. |
| **I'm stuck** | Gives concrete ways to get started and a one-tap 5-minute timer. | Getting started is often the hardest part. A short, allowed-to-stop timer lowers the barrier. |
| **Dump** | A fast brain-dump box, one thought per line. Sort each one later into Today, Later, or delete. | Frees working memory. Capturing a thought is separate from deciding about it. |
| **Focus** | A visual timer (a shrinking wedge) with 5–45 minute presets, a chime, and a log of today's sessions. | Helps with time blindness: you *see* time passing. |
| **Routines** | Morning and evening checklists (editable) with time estimates. They reset every day. | Fewer decisions. The estimates show how long routines really take. |
| **Check-in** | 30-second daily log of energy, mood, meds and sleep, with a 14-day chart and simple averages. | Shows patterns you can bring to your doctor or therapist. |

Every completed step, task, routine item or focus session counts as a **win**. Small, frequent rewards help with ADHD motivation.

## Running it

It's a single HTML file with no dependencies to install.

- **Open it:** double-click `index.html`, or host it anywhere static (e.g. GitHub Pages → Settings → Pages → deploy from `main`, root).
- **Edit it:** change `src/next-step.html`, then run `./build.sh` to regenerate `index.html`.

## Your data

Everything is stored in your browser (`localStorage`). Nothing is sent anywhere. When the page is opened as a Claude artifact, it also saves a private copy to your account so it stays the same across devices.

## Ideas for next versions

- Reminders and notifications (needs a native or PWA wrapper)
- Recurring tasks
- "Body doubling" mode: a shared focus room with a friend
- Export check-in history as CSV for appointments
