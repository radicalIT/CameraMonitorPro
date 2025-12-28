# CameraMonitorPro

A professional-grade "On-Camera" monitoring solution for filmmakers and camera operators, turning any Android tablet or phone into a high-end production monitor.

## Advanced Video Tools
* **Focus Peaking:** Real-time edge detection with adjustable sensitivity and color selection (Red, Green, Blue).
* **Zebra Stripes:** Exposure aid with configurable thresholds (70% or 95%) to identify overexposed areas.
* **False Color:** Comprehensive luminance map to precisely judge exposure across the entire frame.
* **3D LUT Support:** Hardware-accelerated S-Log3 to Rec.709 conversion using OpenGL shaders.
* **Anamorphic Desqueeze:** Support for 1.33x and 1.55x anamorphic lenses.
* **Signal Analysis:** Real-time RGB Parade and Luma Histogram for professional signal monitoring.

## Audio & Interface
* **Audio Monitoring:** Real-time monitoring of USB microphone/camera audio through headphones with safety headset detection.
* **Mirror & Grid:** Toggleable horizontal mirroring and Rule of Thirds grid overlays.
* **Gallery & Snapshots:** Capture high-quality frames directly to the device gallery.

## Technical Implementation
This application processes all video tools directly on the GPU using custom GLSL shaders, ensuring high frame rates and low CPU overhead even with all analysis tools active.

## License
Based on [android-usb-cam-viewer](https://gitlab.com/yaky/android-usb-cam-viewer) by yaky. Licensed under the [Apache License 2.0](LICENSE).