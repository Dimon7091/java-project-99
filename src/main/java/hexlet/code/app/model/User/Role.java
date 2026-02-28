package hexlet.code.app.model.User;

public enum Role {
    USER,
    ADMIN;

    public String getRoleName() {  // ← 2. Метод для Spring Security
        return "ROLE_" + this.name();  // ← 3. Добавляет префикс
    }
}
