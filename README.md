# ToneSpace - Creator Economy for Tones & Wallpapers

A full-featured Android app where users share ringtones, notification tones, alarm tones, and wallpapers. Creators earn money from downloads, plays, and rewarded ads.

## Features

- **Browse & Discover** - Trending, featured, and new sounds organized by category
- **Audio Player** - Full player with waveform visualization, play/pause, skip, like
- **Upload** - Record or upload audio files with metadata, tags, cover images
- **Creator Dashboard** - Track plays, downloads, and earnings
- **Wallet** - View balance, request withdrawals
- **Premium System** - Rewarded ads unlock premium sounds; creators earn 70% revenue share
- **Social** - Like, comment, share, follow creators
- **AdMonetization** - Banner ads, interstitial ads, rewarded ads via AdMob

## Architecture

- **UI**: Jetpack Compose + Material 3
- **DI**: Hilt (Dagger)
- **Database**: Room (local cache) + Firestore (remote)
- **Storage**: Firebase Cloud Storage
- **Auth**: Firebase Auth (Email + Google)
- **Networking**: Retrofit + Kotlin Serialization
- **Audio**: ExoPlayer
- **Images**: Coil
- **CI/CD**: GitHub Actions (builds APK + AAB automatically)

## Monetization Model

| Event | Creator Earnings | App Revenue |
|-------|-----------------|-------------|
| Ad-supported play | $0.001/play | Ad revenue |
| Ad-supported download | $0.01/download | Ad revenue |
| Rewarded ad (unlock premium) | 70% ad value | 30% |
| Direct premium purchase | 70% | 30% |

**Token System**: 1 token = $0.01. Creators withdraw at $10 minimum via Stripe/PayPal.

## Setup

### 1. Firebase Setup (Free Tier)
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project named "ToneSpace"
3. Add Android app with package `com.tonespace.app`
4. Download `google-services.json` and place in `app/` folder
5. Enable these Firebase services (all free tier):
   - **Authentication** (Email/Password + Google)
   - **Cloud Firestore** (create database)
   - **Cloud Storage** (create bucket)
   - **Cloud Messaging** (FCM)
   - **App Check** (Play Integrity)

### 2. AdMob Setup
1. Go to [AdMob Console](https://admob.google.com)
2. Create app with package `com.tonespace.app`
3. Create ad units:
   - Banner ad
   - Interstitial ad
   - Rewarded ad
4. Update `Constants.kt` with your ad unit IDs
5. Update `app/build.gradle.kts` with your AdMob app ID

### 3. GitHub Secrets
Add these secrets in your GitHub repo (Settings > Secrets):

| Secret | Description |
|--------|-------------|
| `GOOGLE_SERVICES_JSON` | Base64 encoded `google-services.json` |
| `KEYSTORE_BASE64` | Base64 encoded release keystore (optional for debug) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

To encode your `google-services.json`:
```bash
base64 -i google-services.json | tr -d '\n'
```

### 4. Build
Push to `main` branch. GitHub Actions will automatically:
- Build debug APK
- Build release APK + AAB (on main branch)
- Upload artifacts for download

Download builds from: Actions > latest workflow run > Artifacts

### 5. Local Build (Optional)
```bash
# Place google-services.json in app/ folder
cp app/google-services.json.example app/google-services.json
# Edit with your Firebase config

# Build debug APK
./gradlew assembleDebug

# Build release AAB
./gradlew bundleRelease
```

## Project Structure

```
tonespace/
├── .github/workflows/build.yml    # CI/CD pipeline
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json.example
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── res/                    # Resources
│       └── java/com/tonespace/app/
│           ├── ToneShareApp.kt     # Application class
│           ├── MainActivity.kt     # Entry point
│           ├── ads/                 # AdMob integration
│           │   └── AdManager.kt
│           ├── data/
│           │   ├── local/          # Room database
│           │   ├── model/          # Data models
│           │   ├── network/        # Retrofit API
│           │   └── repository/     # Data repositories
│           ├── di/                 # Hilt modules
│           ├── player/             # ExoPlayer service
│           ├── ui/
│           │   ├── components/     # Reusable composables
│           │   ├── navigation/     # Nav graph
│           │   ├── screens/        # Feature screens
│           │   └── theme/          # Material 3 theme
│           └── util/               # Utilities
├── build.gradle.kts
├── gradle/wrapper/
└── settings.gradle.kts
```

## Tech Stack

- Kotlin 1.9.20
- Jetpack Compose (BOM 2024.02)
- Material Design 3
- Hilt 2.48
- Room 2.6.1
- Firebase BOM 32.8.0
- Retrofit 2.9.0
- ExoPlayer 2.19.1
- Coil 2.5.0
- GitHub Actions CI/CD

## License

MIT License