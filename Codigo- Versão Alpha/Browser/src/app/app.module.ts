import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule,HttpClient } from '@angular/common/http';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { FormsModule ,ReactiveFormsModule} from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TopBarComponent } from './top-bar/top-bar.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { SimplebarAngularModule } from 'simplebar-angular';
import { AgmCoreModule } from '@agm/core';
import { MapappComponent } from './mapapp/mapapp.component';
import { HomeComponent } from './home/home.component';
import { ParallaxDirective } from './home/parallax.directive';
import { UserComponent } from './user/user.component';
import { SignInComponent } from './user/sign-in/sign-in.component';
import { SignUpComponent } from './user/sign-up/sign-up.component';
import { PersoneComponent } from './person/persone/persone.component';
import { SettingsComponent } from './person/persone/settings/settings.component';
import { RoutsComponent } from './person/persone/routs/routs.component';
import { GalleryComponent } from './person/persone/gallery/gallery.component';
import { RockTrophyComponent } from './person/persone/rock-trophy/rock-trophy.component';
import { JwtModule } from "@auth0/angular-jwt";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CarouselComponent } from './carousel/carousel.component';
import { AgmDirectionModule } from 'agm-direction';
import { CommunityMainComponent } from './community-main/community-main.component';
import { PostItemComponent } from './community-main/post-item/post-item.component';
import { CaminhoComponent } from './mapapp/caminho/caminho.component';
import { MapControllComponent } from './mapapp/map-controll/map-controll.component';
import { DirectionComponent } from './mapapp/caminho/direction/direction.component';
import { AboutusComponent} from './aboutus/aboutus.component';


@NgModule({
  declarations: [
    AppComponent,
    TopBarComponent,
    MapappComponent,
    HomeComponent,
    ParallaxDirective,
    UserComponent,
    SignInComponent,
    SignUpComponent,
    PersoneComponent,
    CarouselComponent,
    CommunityMainComponent,
    PostItemComponent,
    CaminhoComponent,
    MapControllComponent,
    DirectionComponent,
    RoutsComponent,
    GalleryComponent,
    RockTrophyComponent,
    SettingsComponent,
    AboutusComponent
    ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    SimplebarAngularModule,
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyDaxjTT7ejDx8ykQs7UU3_fuKnPLIIztjo', //'AIzaSyBY1VATzvx85tm56FL0C4Agf_gojmbE_XI'
      libraries: ['places', 'drawing', 'geometry'],
    }),
    AgmDirectionModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
        whitelistedDomains: ["http://localhost:4200/person/"],
        blacklistedRoutes: ["http://localhost:4200/map/"]
      }
    }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpTranslateLoader,
        deps: [HttpClient]
      }
    }),
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

// AOT compilation support
export function httpTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

export function tokenGetter() {
  return localStorage.getItem("tokenID");
}
