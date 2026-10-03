#!/usr/bin/env python3
"""Generate the explicit sdp/ssp dimen resources bundled in this library.

Every value follows one formula, identical to the well-known intuit sdp/ssp
libraries (verified value-for-value against com.intuit.sdp/ssp 1.1.1):

    _<n>sdp      =  n * smallestWidth / 300  dp
    _minus<n>sdp = -n * smallestWidth / 300  dp
    _<n>ssp      =  n * smallestWidth / 300  sp

Buckets: default (values/, scale 1.0) plus sw300dp..sw1080dp in 30dp steps.
Ranges:  sdp 1..600, minus sdp 1..60, ssp 1..100.

Output: values[-swXXXdp]/sdp.xml and ssp.xml under each target res directory.
Run from the repo root:  python3 scripts/generate_dimens.py
"""
import pathlib
from decimal import Decimal, ROUND_HALF_UP

REPO = pathlib.Path(__file__).resolve().parent.parent
TARGETS = [
    REPO / "library-android/src/main/res",
    REPO / "library/src/androidMain/res",
]

SDP_POS_MAX = 600
SDP_NEG_MAX = 60
SSP_POS_MAX = 100
BUCKETS = [None] + list(range(300, 1081, 30))  # None = default values/


def scaled(n: int, sw: int) -> str:
    return str((Decimal(n) * sw / Decimal(300)).quantize(Decimal("0.01"), ROUND_HALF_UP))


def write(path: pathlib.Path, header: str, entries: list[tuple[str, str]]):
    lines = ['<?xml version="1.0" encoding="utf-8"?>', f"<!-- {header} -->", "<resources>"]
    lines += [f'    <dimen name="{name}">{value}</dimen>' for name, value in entries]
    lines.append("</resources>")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n")


def main():
    for target in TARGETS:
        for bucket in BUCKETS:
            folder = "values" if bucket is None else f"values-sw{bucket}dp"
            sw = bucket or 300
            label = f"sw{bucket}dp" if bucket else "default (below sw300dp: 1sdp = 1dp)"

            sdp = [(f"_{n}sdp", f"{scaled(n, sw)}dp") for n in range(1, SDP_POS_MAX + 1)]
            sdp += [(f"_minus{n}sdp", f"-{scaled(n, sw)}dp") for n in range(1, SDP_NEG_MAX + 1)]
            write(target / folder / "sdp.xml", f"Scalable dp for {label}. Generated: value = n x smallestWidth / 300 dp.", sdp)

            ssp = [(f"_{n}ssp", f"{scaled(n, sw)}sp") for n in range(1, SSP_POS_MAX + 1)]
            write(target / folder / "ssp.xml", f"Scalable sp for {label}. Generated: value = n x smallestWidth / 300 sp.", ssp)
        print(f"wrote {len(BUCKETS) * 2} files under {target}")


if __name__ == "__main__":
    main()
