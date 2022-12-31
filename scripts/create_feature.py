#!/usr/bin/python3

import sys, os

name = str(sys.argv[1])
feature_package = name.replace("-", ".")

package = f"dev.olog.feature.{feature_package}"

# api module
dirs = package.replace(".", "/")
os.makedirs(f"feature/{name}/api/src/main/java/{dirs}/api", exist_ok=True)

with open(f"feature/{name}/api/src/main/AndroidManifest.xml", "w+") as f:
    f.write(
f"""<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="{package}.api"/>
"""
    )

with open(f"feature/{name}/api/build.gradle", "w+") as f:
    f.write(
f"""plugins {{
    id("dev.olog.msc.library")
}}
"""
    )

# impl module
dirs = package.replace(".", "/")
os.makedirs(f"feature/{name}/impl/src/main/java/{dirs}/impl", exist_ok=True)

with open(f"feature/{name}/impl/src/main/AndroidManifest.xml", "w+") as f:
    f.write(
f"""<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="{package}.impl"/>
"""
    )

with open(f"feature/{name}/impl/build.gradle", "w+") as f:
    f.write(
f"""plugins {{
    id("dev.olog.msc.feature")
}}

dependencies {{
    api(projects.feature.{name}.api)
}}
"""
    )

# settings.gradle
with open(f"settings.gradle", "a+") as f:
    f.write(
f"""
include ":feature:{name}:api"
include ":feature:{name}:impl\""""
)