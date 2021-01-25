# PermissionTerminator

A clean, comprehensive and scalable API to request Android Runtime permissions.

## How to use

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
