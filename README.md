# SDP SSP — Scalable Size Units for Compose

Screen-size-aware `dp` and `sp` units for Jetpack Compose and Compose Multiplatform. Sizes scale continuously with the device's actual screen dimensions so your UI looks consistent across phones, tablets, and desktops.

---

## Modules

| Module | Artifact | Targets |
|---|---|---|
| `library` | `com.sdp.ssp:kmp` | Android · iOS · JVM · WasmJs |
| `library-android` | `com.sdp.ssp:android` | Android only |

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

**Compose Multiplatform** (Android, iOS, JVM, Wasm):
```kotlin
// build.gradle.kts
implementation("com.sdp.ssp:kmp:<version>")
```

**Android only:**
```kotlin
implementation("com.sdp.ssp:android:<version>")
```

> Find the latest `<version>` tag on the [releases page](https://github.com/dbv0610/sdp-ssp-compose/releases).

---

## Usage — Compose Multiplatform (`library`)

Import from `com.sdp.ssp.kmp`.

### Initialize (once at app start)

```kotlin
// Activity.onCreate or Application.onCreate
SDPConfig.setScalingRatio(360.0) // baseline screen width in dp (default: 360)
```

### Composable extensions

```kotlin
import com.sdp.ssp.kmp.SDPConfig
import com.sdp.ssp.kmp.sdp   // layout sizes
import com.sdp.ssp.kmp.ssp   // text sizes
// Also available on Android target:
import com.sdp.ssp.kmp.Sdp   // Intuit-resource-backed layout dp
import com.sdp.ssp.kmp.Ssp   // continuous-scaling text sp

@Composable
fun MyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.sdp)
    ) {
        Box(modifier = Modifier.size(120.sdp, 48.sdp)) {
            Text(
                text = "Hello",
                fontSize = 14.ssp
            )
        }
    }
}
```

| Extension | Receiver types | Returns | Description |
|---|---|---|---|
| `.sdp` | `Int`, `Float`, `Double` | `Dp` | Layout size scaled to screen |
| `.ssp` | `Int`, `Float`, `Double` | `TextUnit` | Text size scaled to screen, respects font accessibility |
| `.Sdp` *(Android)* | `Int` | `Dp` | Layout size via Intuit SDP resources |
| `.Ssp` *(Android)* | `Int` | `TextUnit` | Text size, continuously scaled |

---

## Usage — Android only (`library-android`)

Import from `com.sdp.ssp.android`.

### Composable extensions

```kotlin
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

@Composable
fun MyScreen() {
    Box(
        modifier = Modifier
            .size(120.Sdp, 48.Sdp)
            .padding(16.Sdp)
    ) {
        Text(
            text = "Hello",
            fontSize = 14.Ssp
        )
    }
}
```

### Non-Composable extensions

For use outside Compose (e.g. View-based UI or ViewModel):

```kotlin
import com.sdp.ssp.android.getSdp
import com.sdp.ssp.android.getSsp

val widthPx = 120.getSdp(context)   // Float, dp value
val textPx  = 14.getSsp(context)    // Float, sp value
```

### Screen utility functions

```kotlin
import com.sdp.ssp.android.*

// Composable
val widthDp   = getScreenWidthInDp()       // Dp
val heightDp  = getScreenHeightInDp()      // Dp
val widthPx   = getScreenWidthInPx()       // Int
val heightPx  = getScreenHeightInPx()      // Int
val sizePx    = getScreenSize()            // android.util.Size (px)

// Conversions
val px   = 16.dp.toPx()                   // Float
val dp1  = 48.pxToDp()                    // Dp  (Int receiver)
val dp2  = 48f.pxToDp()                   // Dp  (Float receiver)

// Non-Composable
val inches = getScreenSizeInInches(context)    // Double (diagonal)
val statusBar = context.statusBarHeight        // Int (px)
val navBar    = context.navigationBarHeight    // Int (px)
```

---

## How scaling works

### `sdp` / `Sdp` — layout

```
result = value × min(screenWidthDp, screenHeightDp) / scalingRatio
```

`120.sdp` on a 360 dp screen → 120 dp.
`120.sdp` on a 480 dp screen → 160 dp (proportionally larger).

### `ssp` / `Ssp` — text

Same formula, returned as `sp` so the Android system's accessibility font-scale setting is applied on top:

```
result = value × min(screenWidthDp, screenHeightDp) / scalingRatio   (in sp)
```

---

## Configuration

```kotlin
SDPConfig.setScalingRatio(360.0)
```

The ratio is the reference screen width in dp your design was made for (default `360`). Raise it to shrink all sizes; lower it to grow them.

---

## Platform support

| Platform | `library` (KMP) | `library-android` |
|---|---|---|
| Android | ✓ | ✓ |
| iOS arm64 | ✓ | — |
| iOS Simulator arm64 | ✓ | — |
| JVM Desktop | ✓ | — |
| Wasm (browser) | ✓ | — |

**Minimum Android SDK:** 24
