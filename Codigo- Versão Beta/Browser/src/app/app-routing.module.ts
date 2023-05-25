import { NgModule, Component } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { TopBarComponent } from './top-bar/top-bar.component';
import { MapappComponent } from './mapapp/mapapp.component';
import { UserComponent } from './user/user.component';
import { SignUpComponent } from './user/sign-up/sign-up.component';
import { SignInComponent } from './user/sign-in/sign-in.component';
import { PersoneComponent } from './person/persone/persone.component';
import { AuthGuard } from './auth/auth.guard';
import { MapControllComponent } from './mapapp/map-controll/map-controll.component';
import { CommunityMainComponent } from './community-main/community-main.component';
import { CaminhoComponent } from './mapapp/caminho/caminho.component';
import { AboutusComponent } from './aboutus/aboutus.component';
import { AdminDefaultComponent } from './layouts/admin-default/admin-default.component';
import { DashboardComponent } from './admin-modules/dashboard/dashboard.component';
import { InfosComponent } from './admin-modules/infos/infos.component';
import { AdminManagementComponent } from './admin-modules/admin-management/admin-management.component';
import { UserManagementComponent } from './admin-modules/user-management/user-management.component';
import { MapManagementComponent } from './admin-modules/map-management/map-management.component';
import { AdminSettingsComponent } from './admin-modules/admin-settings/admin-settings.component';


const routes: Routes = [

  {path: 'home',component : TopBarComponent,
  children:[{path : '',component : HomeComponent}]},

  {path: 'community',component : TopBarComponent,
    children:[{path : '',component : CommunityMainComponent}]
  },

  {path : 'map', component : MapappComponent,
  children: [
  {path:'caminho',component : CaminhoComponent , pathMatch:'full'},
  {path:'control',component : MapControllComponent , pathMatch:'full'}
  ]
  },
  
  {
    path :'user',component : UserComponent
  },
  {
    path :'signup',component : TopBarComponent,
    children :[{ path : '',component: SignUpComponent }]
  },

  {
    path :'signin',component : TopBarComponent,
    children :[{ path : '',component: SignInComponent }]
  }, 

  {path: 'admin',component : AdminDefaultComponent,
  children:[{path : '',component : DashboardComponent},
            {path : 'userReports', component: InfosComponent},
            {path : 'adminTools', component: AdminManagementComponent},
            {path : 'userTools', component: UserManagementComponent},
            {path : 'mapTools', component: MapManagementComponent},
            {path: 'settings', component: AdminSettingsComponent}
           ]
  },

  {path:'person',component : TopBarComponent,
  children:[{path : '',component:PersoneComponent, canActivate:[AuthGuard]}
  ]},


  {path: 'aboutus',component : TopBarComponent,
  children:[{path : '',component : AboutusComponent}]},

  {path :'',redirectTo:'home',pathMatch:'full'},
  {path :'*',redirectTo:'home',pathMatch:'full'},
  {path :'**',redirectTo:'home',pathMatch:'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
