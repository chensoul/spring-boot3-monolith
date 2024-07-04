package com.chensoul.sharedlib.security.user;

import java.util.Set;

public interface UserContextPermissionsService {
	UserContextPermissions getUserContextByUserIdAndContextId(UserContextRequest userContextRequest);

	default UserContextPermissions addPermissions(String userId, String contextId, Set<String> permissions) {
		return addPermissions(userId, contextId, null, permissions);
	}

	default UserContextPermissions addPermissions(String userId, String contextId, String roleId, Set<String> permissions) {
		return UserContextPermissions.builder()
			.userId(userId)
			.contextId(contextId)
			.permissions(permissions)
			.build();
	}
}
