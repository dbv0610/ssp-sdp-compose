# SDP SSP — Scalable Size Units for Compose

Screen-size-aware `dp` and `sp` units for Jetpack Compose and Compose Multiplatform.  
Sizes scale continuously with the device's actual screen so your UI looks consistent across all phones, tablets, and desktops.

---

## Modules

| Module | Maven coordinate | Targets |
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

**Compose Multiplatform** (Android · iOS · JVM · Wasm):
```kotlin
// build.gradle.kts
implementation("com.github.dbv0610.ssp-sdp-compose:android:kmp:<version>")
```

**Android only:**
```kotlin
implementation("com.github.dbv0610.ssp-sdp-compose:android:<version>")
```

> Replace `<version>` with the latest release tag from the [releases page](https://github.com/dbv0610/sdp-ssp-compose/releases).

---

## Compose Multiplatform — `com.sdp.ssp:kmp`

Package: **`com.sdp.ssp.kmp`**

### Initialize once at app start

```kotlin
import com.sdp.ssp.kmp.SDPConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDPConfig.setScalingRatio(360.0) // your design baseline in dp (default: 360)
    }
}
```

### Extensions

```kotlin
import com.sdp.ssp.kmp.sdp  // layout
import com.sdp.ssp.kmp.ssp  // text

@Composable
fun MyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.sdp),
        verticalArrangement = Arrangement.spacedBy(12.sdp)
    ) {
        Box(modifier = Modifier.size(120.sdp, 48.sdp)) {
            Text(text = "Hello", fontSize = 14.ssp)
        }
    }
}
```

Also available on the **Android target only** (backed by explicit per-screen-width dimen resources bundled in the library — no external dependency):

```kotlin
import com.sdp.ssp.kmp.Sdp  // layout  (Int only)
import com.sdp.ssp.kmp.Ssp  // text    (Int only)

Box(modifier = Modifier.size(120.Sdp, 48.Sdp)) {
    Text(text = "Hello", fontSize = 14.Ssp)
}
```

### Extension reference

| Extension | Receiver | Returns | Notes |
|---|---|---|---|
| `.sdp` | `Int`, `Float`, `Double` | `Dp` | Layout size, scales with screen |
| `.ssp` | `Int`, `Float`, `Double` | `TextUnit` | Text size, scales with screen + font accessibility |
| `.Sdp` | `Int` | `Dp` | Android only — bundled sdp resource buckets |
| `.Ssp` | `Int` | `TextUnit` | Android only — continuous screen scaling |

---

## Android only — `com.sdp.ssp:android`

Package: **`com.sdp.ssp.android`**

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
        Text(text = "Hello", fontSize = 14.Ssp)
    }
}
```

### Non-Composable (View / ViewModel)

```kotlin
import com.sdp.ssp.android.getSdp
import com.sdp.ssp.android.getSsp

val layoutDp = 120.getSdp(context)  // Float
val textSp   = 14.getSsp(context)   // Float
```

### Screen utilities

```kotlin
import com.sdp.ssp.android.*

// Composable
val widthDp  : Dp   = getScreenWidthInDp()
val heightDp : Dp   = getScreenHeightInDp()
val widthPx  : Int  = getScreenWidthInPx()
val heightPx : Int  = getScreenHeightInPx()
val sizePx          = getScreenSize()          // android.util.Size

// Unit conversions (Composable)
val px  : Float = 16.dp.toPx()
val dp1 : Dp    = 48.pxToDp()                 // Int receiver
val dp2 : Dp    = 48f.pxToDp()                // Float receiver

// Non-Composable
val diagonal : Double = getScreenSizeInInches(context)
val statusBar: Int    = context.statusBarHeight
val navBar   : Int    = context.navigationBarHeight
```

---

## How it works

### Layout — `sdp` / `Sdp`

```
result_dp = value × min(screenWidthDp, screenHeightDp) / scalingRatio
```

| Screen | `120.sdp` result |
|---|---|
| 360 dp (baseline) | 120 dp |
| 480 dp | 160 dp |
| 720 dp | 240 dp |

### Text — `ssp` / `Ssp`

Same formula but the value is returned as **`sp`**, so the system's accessibility font-scale is automatically applied on top:

```
result_sp = value × min(screenWidthDp, screenHeightDp) / scalingRatio
```

---

## Configuration

```kotlin
SDPConfig.setScalingRatio(360.0)
```

Set this to the screen width in dp that your design was created for. The default is `360`. Increase to shrink all sizes; decrease to grow them.

---

## IDE plugin — inline computed values

The repo ships an IntelliJ/Android Studio plugin (`idea-plugin/`) that renders the
computed value right at the call site as an inlay hint:

```kotlin
Modifier.paddingBottom(12.Sdp)   // shown in the editor as: 12.Sdp 420 ×1.4 → 16.8dp
Text("Hi", fontSize = 14.ssp)    // shown in the editor as: 14.ssp 420 ×1.17 → 16.33sp
```

Build and install:

1. `./gradlew -p idea-plugin buildPlugin`
2. In Android Studio: **Settings | Plugins | ⚙ | Install Plugin from Disk…** and pick
   `idea-plugin/build/distributions/sdp-ssp-inlay-hints-<version>.zip`
3. The device width used for the computation defaults to **420dp**; change it in
   **Settings | Tools | SDP/SSP Hints** to see the values for your device.

---

## Platform support

| Platform | `com.sdp.ssp:kmp` | `com.sdp.ssp:android` |
|---|---|---|
| Android | ✓ | ✓ |
| iOS arm64 | ✓ | — |
| iOS Simulator arm64 | ✓ | — |
| JVM Desktop | ✓ | — |
| Wasm (browser) | ✓ | — |

**Minimum Android SDK:** 24
