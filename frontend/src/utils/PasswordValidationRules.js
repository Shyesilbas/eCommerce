export const passwordValidationRules = (password) => {
    return {
        minLength: password.length >= 6,
        hasUppercase: /[A-Z]/.test(password),
        hasLowercase: /[a-z]/.test(password),
        hasNumber: /\d/.test(password),
        hasSpecialChar: /[!@#$%^&*.]/.test(password),
    };
};

export const isPasswordValid = (password) => {
    const rules = passwordValidationRules(password);
    return Object.values(rules).every(rule => rule);
};