import Vue from 'vue'
import VueRouter from 'vue-router'
import deviceList from '../components/DeviceList.vue'
import login from '../components/Login.vue'
import userManager from '../components/UserManager.vue'

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(VueRouter)

export default new VueRouter({
  mode:'hash',
  routes: [
    {
      path: '/',
      name: 'home',
      component: deviceList,
      redirect: '/deviceList',
      children: [
        {
          path: '/deviceList',
          component: deviceList,
        },
        {
          path: '/userManager',
          name: 'userManager',
          component: userManager,
        }
        ]
    },
    {
      path: '/login',
      name: '登录',
      component: login,
    },
  ]
})
