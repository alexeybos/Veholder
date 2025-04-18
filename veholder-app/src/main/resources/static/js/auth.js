// После успешного логина (сохранение токена в localStorage)
fetch('/info', {
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
})
.then(response => {
    if (!response.ok) {
        return response.json().then(err => Promise.reject(err));
    }
    return response.json();
})
.then(data => {
    // Вставляем данные в HTML
    document.getElementById('user-info').innerHTML = `
        <h1>Welcome, ${data.username}!</h1>
        <p>${data.message}</p>
    `;
})
.catch(error => {
    console.error("Error fetching /info:", error);
    window.location.href = '/login'; // Редирект, если токен невалиден
});