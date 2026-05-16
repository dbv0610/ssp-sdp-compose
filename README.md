# SDP SSP — Scalable Size Units for Compose

A Kotlin library that provides screen-size-aware `dp` and `sp` units for Jetpack Compose and Compose Multiplatform. Text and layout sizes scale continuously with the device's actual screen size, so UI looks consistent across phones, tablets, and desktops without breakpoint hacks.

## Modules

| Module | Targets | Import |
|---|---|---|
| `library` | Android, iOS, JVM, WasmJs | `com.ssp.sdp:library` |
| `library-android` | Android only | `com.ssp.sdp:sdp-ssp-android` |

---

## Setup

### 1. Add JitPack repository

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

### 2. Add dependency

**Compose Multiplatform (Android · iOS · JVM · Wasm):**
```kotlin
// build.gradle.kts
implementation("com.ssp.sdp:library:<version>")
```

**Android only:**
```kotlin
implementation("com.ssp.sdp:sdp-ssp-android:<version>")
```

> Replace `<version>` with the latest release tag from the repository.

---

## Usage

### Compose Multiplatform (`library`)

```kotlin
import com.sdp.ssp.kmp.SDPConfig
import com.sdp.ssp.kmp.sdp
import com.sdp.ssp.kmp.ssp

// Set baseline screen width once at app start (default is 360)
SDPConfig.setScalingRatio(360.0)

@Composable
fun MyScreen() {
    Box(
        modifier = Modifier
            .size(120.sdp)          // scales with screen size
            .padding(16.sdp)
    ) {
        Text(
            text = "Hello",
            fontSize = 14.ssp       // scales with screen size + respects font accessibility setting
        )
    }
}
```

Also works with `Float` and `Double`:
```kotlin
12.5f.sdp
0.5.sdp
```

---

### Android only (`library-android`)

Provides two extension sets:

| Extension | Type | Description |
|---|---|---|
| `Int.Sdp` | `Dp` | Layout size scaled by screen width via Intuit SDP resources |
| `Int.Ssp` | `TextUnit` | Text size scaled continuously by screen width |
| `Int.getSdp(context)` | `Float` | Non-composable layout size |
| `Int.getSsp(context)` | `Float` | Non-composable text size |

```kotlin
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

@Composable
fun MyScreen() {
    Box(
        modifier = Modifier
            .size(120.Sdp)
            .padding(16.Sdp)
    ) {
        Text(
            text = "Hello",
            fontSize = 14.Ssp
        )
    }
}
```

---

## How it works

### `sdp` / `Sdp` — Scalable DP

Layout sizes are scaled proportionally to the device's smallest screen dimension relative to a 360 dp baseline:

```
result_dp = value × (min(screenWidth, screenHeight) / scalingRatio)
```

A box of `120.sdp` on a 360 dp screen = 120 dp.  
On a 480 dp screen = 160 dp — proportionally larger, so layout fills the same visual fraction.

### `ssp` / `Ssp` — Scalable SP

Text sizes follow the same proportional formula but are returned as `sp` units, so the system's accessibility font-scale setting is still respected on top of the screen-size scaling:

```
result_sp = value × (min(screenWidth, screenHeight) / scalingRatio)
```

Because the result is a real `sp` value (not a dp-to-sp conversion), users who increase font size in accessibility settings will see text grow as expected.

---

## Configuration

```kotlin
// Call once before your first Composable, e.g. in Application.onCreate or Activity.onCreate
SDPConfig.setScalingRatio(360.0)  // baseline screen width in dp (default: 360)
```

Increase the ratio to make all sizes smaller relative to the screen; decrease it to make them larger.

---

## Platform support

| Platform | Supported |
|---|---|
| Android | ✓ |
| iOS (arm64, simulatorArm64) | ✓ |
| JVM Desktop | ✓ |
| Wasm (browser) | ✓ |

Minimum Android SDK: **24**
