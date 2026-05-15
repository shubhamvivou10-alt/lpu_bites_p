const state = {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    currentRestaurant: null
};

const ui = {
    showView(view) {
        document.querySelectorAll('[id^="view-"]').forEach(v => v.classList.add('hidden'));
        document.getElementById(`view-${view}`).classList.remove('hidden');
        
        if (view === 'student') {
            document.getElementById('restaurant-list').classList.remove('hidden');
            document.getElementById('menu-view').classList.add('hidden');
            document.getElementById('orders-view').classList.add('hidden');
            this.loadRestaurants();
        }
        if (view === 'admin') this.loadAdminOrders();
    },

    toggleAuth(toLogin) {
        document.getElementById('login-form').classList.toggle('hidden', !toLogin);
        document.getElementById('register-form').classList.toggle('hidden', toLogin);
        document.getElementById('auth-title').innerText = toLogin ? 'CampusBites' : 'Join CampusBites';
    },

    async loadRestaurants() {
        const list = await api.restaurants.list();
        const container = document.getElementById('restaurant-list');
        container.innerHTML = '';
        
        list.forEach(res => {
            const card = document.createElement('div');
            card.className = 'glass card';
            const imgSrc = res.image.startsWith('http') ? res.image : `assets/${res.image}`;
            card.innerHTML = `
                <img src="${imgSrc}" class="restaurant-img" onerror="this.src='https://via.placeholder.com/400x180?text=Restaurant'">
                <h3>${res.name}</h3>
                <p>${res.location}</p>
            `;
            card.onclick = () => this.showMenu(res);
            container.append(card);
        });

        this.loadAIHint();
    },

    async loadAIHint() {
        const hint = await api.ai.recommend('sunny');
        const el = document.getElementById('ai-suggestion');
        el.innerText = hint.suggestion;
        el.classList.remove('hidden');
    },

    async showMenu(res) {
        state.currentRestaurant = res;
        document.getElementById('restaurant-list').classList.add('hidden');
        document.getElementById('menu-view').classList.remove('hidden');
        document.getElementById('current-res-name').innerText = res.name;
        
        const items = await api.restaurants.getMenu(res.id);
        const container = document.getElementById('menu-list');
        container.innerHTML = '';
        
        items.forEach(item => {
            const card = document.createElement('div');
            card.className = 'glass card';
            const imgSrc = item.image.startsWith('http') ? item.image : `assets/${item.image}`;
            card.innerHTML = `
                <img src="${imgSrc}" class="restaurant-img" onerror="this.src='https://via.placeholder.com/400x180?text=Food'">
                <span class="badge badge-${item.type.toLowerCase().replace('-', '')}">${item.type}</span>
                <h3>${item.name}</h3>
                <p class="price">₹${item.price}</p>
                <p>Prep: ${item.prepTime} mins</p>
                <button onclick="ui.placeOrder('${item.id}', '${item.name}', ${item.price})">Order Now</button>
            `;
            container.append(card);
        });
    },

    async placeOrder(itemId, itemName, price) {
        if (!confirm(`Place order for ${itemName}?`)) return;
        
        const res = await api.orders.place({
            userId: state.user.userId,
            hostelBlock: state.user.hostelBlock || 'BH-1',
            restaurantId: state.currentRestaurant.id,
            restaurantLocation: state.currentRestaurant.location,
            items: itemName,
            totalAmount: price.toString()
        });

        alert(`Order Placed! Estimated delivery: ${res.estimatedTime} mins`);
        this.showOrders();
    },

    async showOrders() {
        this.showView('student');
        document.getElementById('restaurant-list').classList.add('hidden');
        document.getElementById('menu-view').classList.add('hidden');
        document.getElementById('orders-view').classList.remove('hidden');
        
        const orders = await api.orders.userOrders(state.user.userId);
        const container = document.getElementById('user-orders-list');
        container.innerHTML = '';
        
        orders.forEach(o => {
            const card = document.createElement('div');
            card.className = 'glass card';
            card.innerHTML = `
                <h4>Order #${o.id.substring(0, 8)}</h4>
                <p>Status: <strong>${o.status}</strong></p>
                <p>Total: ₹${o.totalAmount}</p>
                <p>Est. Time: ${o.estTime} mins</p>
                ${o.status === 'DELIVERED' ? `<button onclick="ui.openReview('${o.id}', '${o.restaurantId}')">Leave Review</button>` : ''}
            `;
            container.append(card);
        });
    },

    async loadAdminOrders() {
        const orders = await api.orders.all();
        const container = document.getElementById('admin-orders-list');
        container.innerHTML = '';
        
        let totalRevenue = 0;
        orders.forEach(o => {
            if (o.status === 'DELIVERED') totalRevenue += parseFloat(o.totalAmount);
            
            const card = document.createElement('div');
            card.className = 'glass card';
            card.innerHTML = `
                <h4>Order #${o.id.substring(0, 8)}</h4>
                <p>Status: ${o.status}</p>
                <p>Amount: ₹${o.totalAmount}</p>
                <select onchange="ui.updateStatus('${o.id}', this.value)">
                    <option ${o.status === 'PENDING' ? 'selected' : ''}>PENDING</option>
                    <option ${o.status === 'PREPARING' ? 'selected' : ''}>PREPARING</option>
                    <option ${o.status === 'OUT_FOR_DELIVERY' ? 'selected' : ''}>OUT_FOR_DELIVERY</option>
                    <option ${o.status === 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                </select>
            `;
            container.append(card);
        });

        document.getElementById('stat-revenue').innerText = `₹${totalRevenue}`;
        document.getElementById('stat-orders').innerText = orders.length;
        const resList = await api.restaurants.list();
        document.getElementById('stat-joints').innerText = resList.length;
    },

    async updateStatus(id, status) {
        await api.orders.updateStatus({ orderId: id, status });
        alert('Status updated');
        this.loadAdminOrders();
    },

    openReview(orderId, resId) {
        this.showModal('Rate your experience', `
            <textarea id="review-text" style="width:100%; height:100px; border-radius:12px; padding:10px;" placeholder="How was the food?"></textarea>
        `, 'Submit', async () => {
            const text = document.getElementById('review-text').value;
            const res = await api.reviews.save({
                orderId,
                userId: state.user.userId,
                restaurantId: resId,
                reviewText: text
            });
            alert(`Review tagged as: ${res.sentiment}`);
            this.closeModal();
        });
    },

    openAddRestaurant() {
        this.showModal('Add Food Joint', `
            <input type="text" id="new-res-name" placeholder="Restaurant Name" style="margin-bottom:10px">
            <input type="text" id="new-res-loc" placeholder="Location (e.g. Uni-Mall)">
        `, 'Save', async () => {
            await api.restaurants.save({
                name: document.getElementById('new-res-name').value,
                location: document.getElementById('new-res-loc').value
            });
            this.closeModal();
            this.loadAdminOrders();
        });
    },

    async openAddMenu() {
        const resList = await api.restaurants.list();
        let options = resList.map(r => `<option value="${r.id}">${r.name}</option>`).join('');
        
        this.showModal('Add Menu Item', `
            <select id="new-item-res" style="margin-bottom:10px">${options}</select>
            <input type="text" id="new-item-name" placeholder="Item Name" style="margin-bottom:10px">
            <input type="number" id="new-item-price" placeholder="Price" style="margin-bottom:10px">
            <select id="new-item-type" style="margin-bottom:10px">
                <option>VEG</option>
                <option>NON-VEG</option>
            </select>
            <input type="number" id="new-item-prep" placeholder="Prep Time (mins)">
        `, 'Add Item', async () => {
            await api.restaurants.saveMenu({
                restaurantId: document.getElementById('new-item-res').value,
                name: document.getElementById('new-item-name').value,
                price: document.getElementById('new-item-price').value,
                type: document.getElementById('new-item-type').value,
                prepTime: document.getElementById('new-item-prep').value
            });
            this.closeModal();
        });
    },

    showModal(title, body, actionText, actionFn) {
        document.getElementById('modal-title').innerText = title;
        document.getElementById('modal-body').innerHTML = body;
        const btn = document.getElementById('modal-action-btn');
        btn.innerText = actionText;
        btn.onclick = actionFn;
        document.getElementById('app-modal').style.display = 'flex';
    },

    closeModal() {
        document.getElementById('app-modal').style.display = 'none';
    }
};

const auth = {
    async register() {
        const data = {
            name: document.getElementById('reg-name').value,
            regNo: document.getElementById('reg-no').value,
            hostelBlock: document.getElementById('reg-hostel').value,
            phone: document.getElementById('reg-phone').value,
            role: document.getElementById('reg-role').value,
            password: document.getElementById('reg-pass').value
        };
        await api.auth.register(data);
        alert('Registration successful! Please login.');
        ui.toggleAuth(true);
    },

    async login() {
        const data = {
            regNo: document.getElementById('login-reg').value,
            password: document.getElementById('login-pass').value
        };
        const res = await api.auth.login(data);
        state.user = res;
        state.token = res.token;
        localStorage.setItem('user', JSON.stringify(res));
        localStorage.setItem('token', res.token);
        
        document.getElementById('user-display').innerText = `Welcome, ${res.name}`;
        ui.showView(res.role === 'ADMIN' ? 'admin' : 'student');
    },

    logout() {
        state.user = null;
        state.token = null;
        localStorage.clear();
        ui.showView('auth');
    }
};

// Init
window.onload = () => {
    if (state.user) {
        document.getElementById('user-display').innerText = `Welcome, ${state.user.name}`;
        ui.showView(state.user.role === 'ADMIN' ? 'admin' : 'student');
    } else {
        ui.showView('auth');
    }
};
