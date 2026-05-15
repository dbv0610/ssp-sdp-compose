package com.github.dongb2002.sdpssp.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.dongb2002.sdpssp.SDPConfig
import com.github.dongb2002.sdpssp.Sdp
import com.github.dongb2002.sdpssp.Ssp
import com.github.dongb2002.sdpssp.sdp
import com.github.dongb2002.sdpssp.ssp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDPConfig.setScalingRatio(360.0)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                SampleScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("SDP / SSP Demo") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.Sdp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.Sdp)
        ) {
            Spacer(modifier = Modifier.height(8.Sdp))

            // ── SDP ────────────────────────────────────────────────────
            SectionLabel("SDP (Intuit)  vs  sdp (Library)")
            listOf(40, 80, 120).forEach { value ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.Sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(value.Sdp, 28.Sdp)
                            .background(Color(0xFF1565C0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${value}.Sdp", color = Color.White, fontSize = 9.Ssp)
                    }
                    Box(
                        modifier = Modifier
                            .size(value.sdp, 28.sdp)
                            .background(Color(0xFF2E7D32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${value}.sdp", color = Color.White, fontSize = 9.ssp)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.Sdp))

            // ── SSP ────────────────────────────────────────────────────
            SectionLabel("SSP (Intuit)  vs  ssp (Library)")
            listOf(12, 18, 24, 32).forEach { value ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.Sdp)
                ) {
                    Text(
                        text = "${value}.Ssp",
                        fontSize = value.Ssp,
                        color = Color(0xFF1565C0)
                    )
                    Text(
                        text = "${value}.ssp",
                        fontSize = value.ssp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.Sdp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleSmall)
}
