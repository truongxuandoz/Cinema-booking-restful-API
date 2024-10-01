	package cinema.ticket.booking.controller;

import java.security.Principal;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;


import cinema.ticket.booking.exception.MyBadRequestException;
import cinema.ticket.booking.exception.MyConflictExecption;
import cinema.ticket.booking.exception.MyNotFoundException;
import cinema.ticket.booking.model.Role;
import cinema.ticket.booking.model.enumModel.ERole;
import cinema.ticket.booking.request.NewPasswordRequest;
import cinema.ticket.booking.response.AccountSummaryResponse;
import cinema.ticket.booking.response.ErrorResponse;
import cinema.ticket.booking.response.MyApiResponse;
import cinema.ticket.booking.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/user")
@Tag(name="2. User Endpoint")
public class UserController {
	
	@Autowired
	private UserService userSER;
	
	
	@GetMapping("/getall")
	@Operation(
         summary = "Get all users' infomation (Admin is required)",
         responses = {
    		 @ApiResponse( responseCode = "200", description = "A list of users' information.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = AccountSummaryResponse.class))),
    		 @ApiResponse( responseCode = "401", description = "Invalid token.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "403", description = "User do not have permission to get this data.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
         },
         parameters = {
             @Parameter( name = "Authorication", in = ParameterIn.HEADER,
		                   schema = @Schema(type = "string"), example = "Bearer <token>",
		                   required = true
             )
        }
	)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getall() {
		return ResponseEntity.ok(userSER.getUsers());
	}
	

	@GetMapping("/info/{username}")
	@Operation(
        summary = "Get information of user (Admin is required)",
		responses = {
    		 @ApiResponse( responseCode = "200", description = "Infomation of user.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = UserDetails.class))),
    		 @ApiResponse( responseCode = "401", description = "Invalid token.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "403", description = "User do not have permission to get this data.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "404", description = "Username not found.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
	     },
         parameters = {
             @Parameter( name = "Authorication", in = ParameterIn.HEADER,
		                   schema = @Schema(type = "string"), example = "Bearer <token>",
		                   required = true
             )
        }
	)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getUserByUsername(@PathVariable(value = "username") String username) {
		return ResponseEntity.ok(userSER.loadUserByUsername(username));
	}
	
	
	@GetMapping("/search")
	@Operation(
         summary = "Search User by username (Admin is required)",
		 responses = {
    		 @ApiResponse( responseCode = "200", description = "A list of users.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = AccountSummaryResponse.class))),
    		 @ApiResponse( responseCode = "401", description = "Invalid token.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "403", description = "User do not have permission to get this data.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
	     },
         parameters = {
             @Parameter( name = "Authorication", in = ParameterIn.HEADER,
		                   schema = @Schema(type = "string"), example = "Bearer <token>",
		                   required = true
             )
        }
	)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> searchUserByUsername(@RequestParam String username) {
		return ResponseEntity.ok(userSER.searchByName(username));	
	}
	
	
	@DeleteMapping("/delete")
	@Operation(
	     summary = "Delete User (Super admin is required)",
		 responses = {
    		 @ApiResponse( responseCode = "200", description = "Deleted user successfully.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = MyApiResponse.class))),
    		 @ApiResponse( responseCode = "400", description = "User has role `Admin` can not be deleted by another user has same role.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "401", description = "Invalid token.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "403", description = "User do not have permission to get this data.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "404", description = "User not found.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
		 },
         parameters = {
             @Parameter( name = "Authorication", in = ParameterIn.HEADER,
		                   schema = @Schema(type = "string"), example = "Bearer <token>",
		                   required = true
             )
        }
	)
	@PreAuthorize("hasRole('ADMIN')")
	public MyApiResponse deleteUserByUsername(@RequestParam String username) {
		if (!userSER.UsernameIsExisted(username))
			throw new MyNotFoundException("User not found");
		
		if (userSER.userHaveRole(username, ERole.ROLE_ADMIN)) 
			throw new MyBadRequestException("This request just can be performed by super admin");
		
		userSER.deteleUserByUsername(username);
		return new MyApiResponse("Removed user `" + username + "`");
	}
	
	
	@GetMapping("/giveadmin")
	@Operation(
         summary = "Give role `Admin` to user (Super admin is required)",
		 responses = {
    		 @ApiResponse( responseCode = "200", description = "Give role to user successfully.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = MyApiResponse.class))),
    		 @ApiResponse( responseCode = "401", description = "Invalid token.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "403", description = "User do not have permission to get this data.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "404", description = "User not found.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    		 @ApiResponse( responseCode = "409", description = "User already has this role.",
				content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
		 },
         parameters = {
             @Parameter( name = "Authorication", in = ParameterIn.HEADER,
		                   schema = @Schema(type = "string"), example = "Bearer <token>",
		                   required = true
             )
        }
	)
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public MyApiResponse giveAdmin(@RequestParam String username) {
		if (userSER.UsernameIsExisted(username)) {
			if (userSER.userHaveRole(username, ERole.ROLE_ADMIN)) 
				throw new MyConflictExecption("User already have this role");
			
			userSER.addRoleToUser(username, ERole.ROLE_ADMIN);
			return new MyApiResponse("Give role to user");
		}
		throw new MyNotFoundException("User not found");
	}
	
	
	@GetMapping("/removeadmin")
	@Operation(
		 summary = "Remove role `Admin` from user (Super admin is required)",
		 responses = {
			 @ApiResponse( responseCode = "200", description = "Remove role from user successfully.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = MyApiResponse.class))),
			 @ApiResponse( responseCode = "400", description = "User did not have `admin` role.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			 @ApiResponse( responseCode = "401", description = "Invalid token.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			 @ApiResponse( responseCode = "403", description = "User do not have permission to get this data.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			 @ApiResponse( responseCode = "404", description = "User not found.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
		 },
		 parameters = {
		     @Parameter( name = "Authorication", in = ParameterIn.HEADER,
		                   schema = @Schema(type = "string"), example = "Bearer <token>",
		                   required = true
		     )
		}
	)
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public MyApiResponse removeAdmin(@RequestParam String username) {
		if (userSER.UsernameIsExisted(username)) {
			if (!userSER.userHaveRole(username, ERole.ROLE_ADMIN)) 
				throw new MyBadRequestException("User do not have this role");
			
			userSER.removeRoleUser(username, ERole.ROLE_ADMIN);
			return new MyApiResponse("Reomved role Admin for user `" + username + "`");
		}
		throw new MyNotFoundException("User not found");
	}
	
	
	@GetMapping("/me")
	@Operation(
		 summary = "Get user's infomation (User is required)",
		 responses = {
			 @ApiResponse( responseCode = "200", description = "User's infomation.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = AccountSummaryResponse.class))),
			 @ApiResponse( responseCode = "401", description = "Invalid token.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			 @ApiResponse( responseCode = "403", description = "User do not have permission to get this data.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
		 },
		 parameters = {
		     @Parameter( name = "Authorication", in = ParameterIn.HEADER,
		                   schema = @Schema(type = "string"), example = "Bearer <token>",
		                   required = true
		     )
		}
	)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<AccountSummaryResponse> getMe(Principal principal) {
		return ResponseEntity.ok(userSER.getUserByName(principal.getName()));
	}
	
	
	@GetMapping("/roles")
	@Operation(
		 summary = "Get user's roles (User is required)",
		 responses = {
			 @ApiResponse( responseCode = "200", description = "User's roles.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = Role.class))),
			 @ApiResponse( responseCode = "401", description = "Invalid token.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			 @ApiResponse( responseCode = "403", description = "User do not have permission to get this data.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
		 },
		 parameters = {
		     @Parameter( name = "Authorication", in = ParameterIn.HEADER,
		                   schema = @Schema(type = "string"), example = "Bearer <token>",
		                   required = true
		     )
		}
	)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Collection<Role>> getRoles(Principal principal) {
		return ResponseEntity.ok(userSER.getRoleFromUser(principal.getName()));
	}
	
	
	@GetMapping("/forgotpassword")
	@Operation(
		 summary = "Get an URL to recover user's password",
		 responses = {
			 @ApiResponse( responseCode = "200", description = "A recoveried code will be sent to user's mail.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = MyApiResponse.class))),
			 @ApiResponse( responseCode = "404", description = "User not found.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
		 }
	)
	public ResponseEntity<MyApiResponse> forgotPassword(@RequestParam @Valid String username) throws Exception {
		return ResponseEntity.ok(userSER.getURIforgetPassword(username));
	}
	
	
	@GetMapping("/password-reset")
	@Operation(
		 summary = "Verify reset token and show password reset form",
		 responses = {
			 @ApiResponse(responseCode = "200", description = "Token is valid, show password reset form", 
							content = @Content(mediaType = "text/html")),
			 @ApiResponse(responseCode = "404", description = "Invalid or expired token", 
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
		 }
	)
	public ResponseEntity<?> showPasswordResetForm(@RequestParam @Valid String code) {
		try {
			userSER.checkReocveryCode(code.replace(' ', '+'));
			// If valid, return HTML page with password reset form
			String htmlContent = generatePasswordResetHtml(code);
			return ResponseEntity.ok()
				.contentType(MediaType.TEXT_HTML)
				.body(htmlContent);
		} catch (MyNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse("Invalid or expired token"));
		}
	}
	
	
	@PostMapping("/recovery")
	@Operation(
		 summary = "This method allows create new password with code",
		 responses = {
			 @ApiResponse( responseCode = "200", description = "Set a new password succesfully.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = MyApiResponse.class))),
			 @ApiResponse( responseCode = "404", description = "This code is expired or invalid.",
							content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
		 }	  
	)
	public ResponseEntity<MyApiResponse> setPassword(@RequestParam @Valid String code, @RequestBody @Valid NewPasswordRequest password) {
		return ResponseEntity.ok(userSER.setNewPassword(code.replace(' ', '+'), password.getPassword()));
	}
	
	
	private String generatePasswordResetHtml(String code) {
		return "<html><body>"
			+ "<h2>Reset Your Password</h2>"
			+ "<form id='resetForm'>"
			+ "<input type='password' id='password' required>"
			+ "<input type='hidden' id='code' value='" + code + "'>"
			+ "<button type='submit'>Reset Password</button>"
			+ "</form>"
			+ "<script>"
			+ "document.getElementById('resetForm').onsubmit = function(e) {"
			+ "    e.preventDefault();"
			+ "    fetch('/api/user/recovery?code=' + document.getElementById('code').value, {"
			+ "        method: 'POST',"
			+ "        headers: {'Content-Type': 'application/json'},"
			+ "        body: JSON.stringify({password: document.getElementById('password').value})"
			+ "    })"
			+ "    .then(response => response.json())"
			+ "    .then(data => alert(data.message))"
			+ "    .catch(error => alert('Error: ' + error));"
			+ "}"
			+ "</script>"
			+ "</body></html>";
	}
}