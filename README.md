# PermissionTerminator

A clean, comprehensive and scalable API to request Android Runtime permissions.

## How to use

Add it in your root `build.gradle` at the end of repositories :
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency :
```
dependencies {
	implementation 'com.github.ParadiseHell:PermissionTerminator:0.0.1'
}
```

Enjoy yourself :

```kotlin
PermissionTerminator
	.with(this /*View, Fragment, Actiivty, Context*/ )
	.permissions(
		Manifest.permission.RECORD_AUDIO
		// add more permissions
	)
	.request(
		onGranted = { grantedPermissionList->
			// called when all permissions are granted
		},
		onDenied = { grantedPermissionList, deniedPermissionList ->
			// called when at least a permission is denied
		},
		onNeverAsked = { grantedPermissionList, deniedPermissionList, neverAskPermissionList ->
			// called when at least a permission is never asked
		}
	)
```

## Detail API

See the [wiki](https://github.com/ParadiseHell/PermissionTerminator/wiki).

## License

```
Copyright 2021 ParadiseHell

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
