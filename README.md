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

- **Open it:** double-click `index.html`, or host the repo anywhere static.
- **Edit it:** change `src/next-step.html`, then run `./build.sh` to regenerate `index.html`.

## Put it on your Android home screen

The app is an installable web app (PWA). It needs to be served over HTTPS; GitHub Pages is free and easiest.

1. On GitHub: **Settings → Pages → Build and deployment → Source: Deploy from a branch**, choose `main` (or this branch) and `/ (root)`, then **Save**. After a minute you get a URL like `https://<username>.github.io/ADHD-App/`.
2. Open that URL in **Chrome** on your phone.
3. Tap **⋮ → Add to Home screen → Install** (or accept the "Install app" prompt).

It then opens full-screen with its own icon, and it works offline. Long-press the icon for shortcuts to **Brain dump**, **Focus timer** and **Check-in**.

Note: the installed app keeps its data on the phone. It is separate from the copy saved by the Claude artifact version.

## Android app (with home-screen widget)

`android/` is a native Android app that wraps the same UI and adds:

- **A home-screen widget** that shows your next step, with **Done** (marks it off without opening the app), **Focus 10** (starts a 10-minute session on that step) and **+ Dump** buttons, plus today's win count.
- **A focus timer that rings when the phone is locked**, with a countdown in the notification shade.
- **Long-press shortcuts** on the app icon: Brain dump, Focus timer, Check-in.
- **Offline use**: everything is stored on the phone.

### Install it

Every push that touches the app runs the **Android APK** GitHub Action, which builds the app and attaches `next-step.apk` to the **android-latest** release.

1. On your phone, sign in to GitHub in the browser and open this repo → **Releases** → **android-latest**.
2. Download `next-step.apk` and open it. Android will ask you to allow installs from your browser: allow it, then tap **Install**.
3. Long-press an empty spot on your home screen → **Widgets** → **Next Step**, and drag it onto the screen.
4. The first time you start a focus session, allow notifications so the timer can alert you.

New builds install over the old one and keep your data. They are signed with `android/app/nextstep.keystore`, which is committed on purpose so updates match. That's fine for installing on your own phone; for the Play Store you'd need a private key kept out of git.

### Build locally (optional)

With Android Studio, or JDK 17 + the Android SDK: `./build.sh && cd android && ./gradlew assembleRelease`.

## Your data

Everything is stored in your browser (`localStorage`). Nothing is sent anywhere. When the page is opened as a Claude artifact, it also saves a private copy to your account so it stays the same across devices.

## Ideas for next versions

- Reminders and notifications (needs a native or PWA wrapper)
- Recurring tasks
- "Body doubling" mode: a shared focus room with a friend
- Export check-in history as CSV for appointments
