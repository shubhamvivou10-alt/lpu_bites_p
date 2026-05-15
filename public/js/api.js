const api = {
    async request(path, method = 'GET', body = null) {
        const options = {
            method,
            headers: { 'Content-Type': 'application/json' }
        };
        if (body) options.body = JSON.stringify(body);
        
        try {
            const res = await fetch(path, options);
            const data = await res.json();
            if (!res.ok) throw new Error(data.error || 'Request failed');
            return data;
        } catch (err) {
            alert(err.message);
            throw err;
        }
    },

    auth: {
        register: (data) => api.request('/api/auth/register', 'POST', data),
        login: (data) => api.request('/api/auth/login', 'POST', data)
    },

    restaurants: {
        list: () => api.request('/api/restaurants'),
        getMenu: (id) => api.request(`/api/menu?restaurantId=${id}`),
        save: (data) => api.request('/api/restaurants', 'POST', data),
        saveMenu: (data) => api.request('/api/menu', 'POST', data)
    },

    orders: {
        place: (data) => api.request('/api/orders', 'POST', data),
        userOrders: (userId) => api.request(`/api/orders?userId=${userId}`),
        all: () => api.request('/api/admin/orders'),
        updateStatus: (data) => api.request('/api/admin/orders/status', 'PUT', data)
    },

    reviews: {
        save: (data) => api.request('/api/reviews', 'POST', data)
    },

    ai: {
        recommend: (weather) => api.request(`/api/ai/recommend?weather=${weather}`)
    }
};
