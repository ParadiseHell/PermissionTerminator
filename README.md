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
	.with(this /*View, Fragment or Actiivty*/ )
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

## Why to make a same wheel?

As you know, you can find lots of Android Permission Library on github, like
[XXPermissions](https://github.com/getActivity/XXPermissions),
[RxPermissions](https://github.com/tbruyelle/RxPermissions),
[PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher)
and so on. But why do I decide to make a new one? Not because they do not work,
at least they work better than `PermissionTerminator` now. I just think they are
not scalable, especially `PermissionsDispatcher` you don't even know how to have
a secondary encapsulation.

So a open source libray must be scalable, so user can do some meaningful things
base one it, so `PermissionTerminator` does. You can write your own **Behavior**
to handle different situations and you can write your own **Processor** to handle
different permissions (`PermissionTerminator` has done a lot, but you can still
override and add your custom implemetions) without change the source code.

Hope `PermissionTerminator` will give you some other ideas about writing a open
source library.

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
