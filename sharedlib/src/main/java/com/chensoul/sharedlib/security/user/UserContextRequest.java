package com.chensoul.sharedlib.security.user;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserContextRequest {
	String userId;
	String contextId;
	Boolean allRoles;
	String roles;
	String cacheControl;
	String token;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserContextRequest that = (UserContextRequest) o;
		return Objects.equals(userId, that.userId) && Objects.equals(contextId, that.contextId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, contextId);
	}
}
