package id.reshito.backend.modules.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import id.reshito.backend.common.exception.InvalidFirebaseTokenException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseTokenService {
    public FirebaseToken verify(String idToken) throws InvalidFirebaseTokenException {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (Exception e) {
            throw new InvalidFirebaseTokenException();
        }
    }
}
