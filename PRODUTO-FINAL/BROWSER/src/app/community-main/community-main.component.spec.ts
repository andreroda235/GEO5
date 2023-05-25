import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommunityMainComponent } from './community-main.component';

describe('CommunityMainComponent', () => {
  let component: CommunityMainComponent;
  let fixture: ComponentFixture<CommunityMainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommunityMainComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommunityMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
