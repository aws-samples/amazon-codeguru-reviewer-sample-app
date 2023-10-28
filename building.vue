<script>
import transMixinComp from "../../../helper/mixin/translation-comp-mixin";
import { arabicValue, englishValue } from "../../../helper/langTransform";
import successError from "../../../helper/mixin/success&error";
import adminApi from "../../../api/adminAxios";
import Switches from "vue-switches";
import Country from "../general/country";
import Avenue from "../general/avenue";
import City from "../general/city";
import {
  required,
  minLength,
  maxLength,
  integer,
  numeric,
  decimal,
} from "vuelidate/lib/validators";
import Swal from "sweetalert2";
import ErrorMessage from "../../widgets/errorMessage";
import loader from "../../general/loader";
import Multiselect from "vue-multiselect";
// require styles
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
/**
 * Advanced Table component
 */

export default {
  components: {
    Switches,
    ErrorMessage,
    loader,
    Multiselect,
    Country,
    Avenue,
    City,
  },
  mixins: [transMixinComp, successError],
  props: {
    id: { default: "create" },
    companyKeys: { default: [] },
    defaultsKeys: { default: [] },
    isPage: { default: true },
    isVisiblePage: { default: null },
    isRequiredPage: { default: null },
    type: { default: "create" },
    idObjEdit: { default: null },
    isPermission: {},
    url: { default: "/real-estate/buildings" },
  },
  data() {
    return {
      selectedCalculationType: null,
      calculationTypes: [
        { id: 1, label: "Fixed percent for company" },
        { id: 2, label: "Fixed amount for company" },
        { id: 3, label: "Fixed amount for owners" },
      ],
      builds: [],
      cities: [],
      countries: [],
      avenues: [],
      modules: ["sales", "rent"],
      isLoader: false,
      isCustom: false,
      building_id: null,
      create: {
        name: "",
        name_e: "",
        description: "",
        description_e: "",
        land_area: 0,
        building_area: 0,
        construction_year: "",
        module: "sales",
        country_id: null,
        city_id: null,
        avenue_id: null,
        lng: 0,
        lat: 0,
        media: [],
        floors_number: 0,
        mediums_number: 0,
        ground_floors_number: 0,
        vaults_number: 0,
        central_air_conditioning: 0,
        elevators_number: 0,
        electricity_meters_number: 0,
        water_meters_number: 0,
        gas_meters_number: 0,
        buying_price: 0,
        buying_date: "",
        middleman_cost: 0,
        registration_cost: 0,
        building_currency_id: null,
      },
      b_images: [],
      media: {},
      b_showPhoto: "../../../../../images/img-1.png",
      saveImageName: [],
      createVideo: "",
      frameUrl: "",
      idDelete: null,
      errors: {},
      is_disabled: false,
    };
  },
  validations: {
    createVideo: { required },
    create: {
      media: {},
      name: { required, minLength: minLength(2), maxLength: maxLength(255) },
      name_e: { required, minLength: minLength(2), maxLength: maxLength(255) },
      description: { maxLength: maxLength(1000) },
      description_e: { maxLength: maxLength(1000) },
      land_area: { numeric, maxLength: maxLength(9) },
      building_area: { numeric, maxLength: maxLength(9) },
      construction_year: {
        integer,
        minLength: minLength(4),
        maxLength: maxLength(4),
      },
      module: { required },
      country_id: {},
      city_id: {},
      avenue_id: {},
      lng: { decimal },
      lat: { decimal },
      floors_number: { integer, maxLength: maxLength(2) },
      mediums_number: { integer, maxLength: maxLength(2) },
      ground_floors_number: { integer, maxLength: maxLength(2) },
      vaults_number: { integer, maxLength: maxLength(2) },
      central_air_conditioning: { integer, maxLength: maxLength(2) },
      elevators_number: { integer, maxLength: maxLength(2) },
      electricity_meters_number: { integer, maxLength: maxLength(2) },
      water_meters_number: { integer, maxLength: maxLength(2) },
      gas_meters_number: { integer, maxLength: maxLength(2) },
      buying_price: { numeric, maxLength: maxLength(9) },
      buying_date: {},
      middleman_cost: { numeric, maxLength: maxLength(9) },
      registration_cost: { numeric, maxLength: maxLength(9) },
      building_currency_id: {},
    },
  },
  methods: {
    selectCalculationType(calculationType) {
      // Handle the selected calculation type here
      // You can perform actions based on the user's choice
    },
    async getCustomTableFields() {
      this.isCustom = true;
      await adminApi
        .get(`/customTable/table-columns/general_avenues`)
        .then((res) => {
          this.fields = res.data;
        })
        .catch((err) => {
          this.errorFun("Error", "Thereisanerrorinthesystem");
        })
        .finally(() => {
          this.isCustom = false;
        });
    },
    isVisible(fieldName) {
      if (!this.isPage) {
        let res = this.fields.filter((field) => {
          return field.column_name == fieldName;
        });
        return res.length > 0 && res[0].is_visible == 1 ? true : false;
      } else {
        return this.isVisiblePage(fieldName);
      }
    },
    isRequired(fieldName) {
      if (!this.isPage) {
        let res = this.fields.filter((field) => {
          return field.column_name == fieldName;
        });
        return res.length > 0 && res[0].is_required == 1 ? true : false;
      } else {
        return this.isRequiredPage(fieldName);
      }
    },
    getCurrentYear() {
      return new Date().getFullYear();
    },
    defaultData() {
      this.building_id = null;
      this.createVideo = "";
      this.frameUrl = "";
      this.create = {
        name: "",
        name_e: "",
        description: "",
        description_e: "",
        v: 0,
        building_area: 0,
        construction_year: "",
        module: "sales",
        country_id: null,
        city_id: null,
        avenue_id: null,
        lng: 0,
        lat: 0,
        floors_number: 0,
        mediums_number: 0,
        ground_floors_number: 0,
        vaults_number: 0,
        central_air_conditioning: 0,
        elevators_number: 0,
        electricity_meters_number: 0,
        water_meters_number: 0,
        gas_meters_number: 0,
        buying_price: 0,
        buying_date: "",
        middleman_cost: 0,
        registration_cost: 0,
        building_currency_id: null,
      };
      this.$nextTick(() => {
        this.$v.$reset();
      });
      this.errors = {};
      this.is_disabled = false;
    },
    resetModalHidden() {
      this.defaultData();
      this.$bvModal.hide(this.id);
    },
    resetModal() {
      this.defaultData();
      setTimeout(async () => {
        if (this.type != "edit") {
          if (!this.isPage) await this.getCustomTableFields();
          this.$nextTick(() => {
            this.$v.$reset();
          });
          this.getCategory();
        } else {
          if (this.idObjEdit.dataObj) {
            let build = this.idObjEdit.dataObj;
            console.log(build);
            this.errors = {};
            if (this.isVisible("country_id")) this.getCategory();
            if (this.isVisible("city_id")) this.getCity(build.country.id);
            if (this.isVisible("street_id")) this.getStreets(build.avenue.id);
            this.building_id = build.id;
            this.createVideo = "";
            this.frameUrl = build.video_link;
            this.create.name = build.name;
            this.create.name_e = build.name_e;
            this.create.description = build.description;
            this.create.description_e = build.description_e;
            this.create.building_area = build.building_area ?? 0;
            this.create.land_area = build.land_area ?? 0;
            this.create.construction_year = build.construction_year ?? "";
            this.create.module = build.module;
            this.create.country_id = build.country.id;
            this.create.street_id = build.street_id;
            this.create.city_id = build.city.id;
            this.create.avenue_id = build.avenue.id;
            if (this.isVisible("avenue_id")) this.getAvenue();
            this.create.lng = build.lng;
            this.create.lat = build.lat;
            this.create.floors_number = build.floors_number;
            this.create.mediums_number = build.mediums_number;
            this.create.ground_floors_number = build.ground_floors_number;
            this.create.vaults_number = build.vaults_number;
            this.create.central_air_conditioning =
              build.central_air_conditioning;
            this.create.elevators_number = build.elevators_number;
            this.create.electricity_meters_number =
              build.electricity_meters_number;
            this.create.water_meters_number = build.water_meters_number;
            this.create.gas_meters_number = build.gas_meters_number;
            this.create.buying_price = build.buying_price;
            this.create.buying_date = build.buying_date;
            this.create.middleman_cost = build.middleman_cost;
            this.create.registration_cost = build.registration_cost;
            // this.create.building_currency_id = build.building_currency_id;
            this.b_images = build.media ?? [];
            if (this.b_images && this.b_images.length > 0) {
              this.b_showPhoto = this.b_images[this.b_images.length - 1].webp;
            } else {
              this.b_showPhoto = "../../../../../images/img-1.png";
            }
            this.errors = {};
          }
        }
      }, 50);
    },

    showCountryModal() {
      if (this.create.country_id == 0) {
        this.$bvModal.show("country-create");
        this.create.country_id = null;
      } else {
        this.getCity(this.create.country_id);
      }
    },
    /**
     *  create countrie
     */
    async resetForm() {
      this.defaultData();
    },

    AddSubmit() {
      if (!this.create.name) {
        this.create.name = this.create.name_e;
      }
      if (!this.create.name_e) {
        this.create.name_e = this.create.name;
      }
      if (!this.create.description) {
        this.create.description = this.create.description_e;
      }
      if (!this.create.description_e) {
        this.create.description_e = this.create.description;
      }
      if (!this.create.floors_number) {
        this.create.floors_number = this.create.floors_number;
      }
      if (!this.create.mediums_number) {
        this.create.mediums_number = this.create.mediums_number;
      }
      if (!this.create.ground_floors_number) {
        this.create.ground_floors_number = this.create.ground_floors_number;
      }
      if (!this.create.vaults_number) {
        this.create.vaults_number = this.create.vaults_number;
      }
      if (!this.create.central_air_conditioning) {
        this.create.central_air_conditioning =
          this.create.central_air_conditioning;
      }
      if (!this.create.elevators_number) {
        this.create.elevators_number = this.create.elevators_number;
      }
      if (!this.create.electricity_meters_number) {
        this.create.electricity_meters_number =
          this.create.electricity_meters_number;
      }
      if (!this.create.water_meters_number) {
        this.create.water_meters_number = this.create.water_meters_number;
      }
      if (!this.create.gas_meters_number) {
        this.create.gas_meters_number = this.create.gas_meters_number;
      }
      if (!this.create.buying_price) {
        this.create.buying_price = this.create.buying_price;
      }
      if (!this.create.buying_date) {
        this.create.buying_date = this.create.buying_date;
      }
      if (!this.create.middleman_cost) {
        this.create.middleman_cost = this.create.middleman_cost;
      }
      if (!this.create.registration_cost) {
        this.create.registration_cost = this.create.registration_cost;
      }

      this.$v.create.$touch();

      if (this.$v.create.$invalid) {
        return;
      } else {
        console.log("see here", this.create);
        this.isLoader = true;
        this.errors = {};
        this.is_disabled = false;

        if (this.type != "edit") {
          adminApi
            .post(this.url, {
              ...this.create,
              company_id: this.$store.getters["auth/company_id"],
            })
            .then((res) => {
              this.is_disabled = true;
              this.building_id = res.data.data.id;
              if (!this.isPage) this.$emit("created");
              else this.$emit("getDataTable");
              setTimeout(() => {
                this.successFun("Addedsuccessfully");
              }, 500);
            })
            .catch((err) => {
              if (err.response.data) {
                this.errors = err.response.data.errors;
              } else {
                this.errorFun("Error", "Thereisanerrorinthesystem");
              }
            })
            .finally(() => {
              this.isLoader = false;
            });
        } else {
          adminApi
            .put(`${this.url}/${this.idObjEdit.idEdit}`, {
              ...this.create,
              company_id: this.$store.getters["auth/company_id"],
            })
            .then((res) => {
              this.$bvModal.hide(this.id);
              this.$emit("getDataTable");
              setTimeout(() => {
                this.successFun("Editsuccessfully");
              }, 500);
            })
            .catch((err) => {
              if (err.response.data) {
                this.errors = err.response.data.errors;
              } else {
                this.errorFun("Error", "Thereisanerrorinthesystem");
              }
            })
            .finally(() => {
              this.isLoader = false;
            });
        }
      }
    },
    async getCategory() {
      this.create.city_id = null;
      this.create.avenue_id = null;
      this.cities = [];
      this.avenues = [];
      await adminApi
        .get(`/countries/get-drop-down?is_active=active`)
        .then((res) => {
          res.data.data.unshift({
            id: 0,
            name: "اضافة دولة",
            name_e: "Add Country",
          });
          this.countries = res.data.data;
        })
        .catch((err) => {
          Swal.fire({
            icon: "error",
            title: `${this.$t("general.Error")}`,
            text: `${this.$t("general.Thereisanerrorinthesystem")}`,
          });
        });
    },
    async getCity(id = null) {
      if (this.create.city_id == 0) {
        this.$bvModal.show("city-create");
        this.create.city_id = null;
      } else if (this.create.city_id) {
        this.create.avenue_id = null;
        let country = this.create.country_id;
        let city = this.create.city_id;
        await adminApi
          .get(`/avenues/get-drop-down?country_id=${country}&city_id=${city}`)
          .then((res) => {
            let l = res.data.data;
            l.unshift({ id: 0, name: "اضافة منطقه", name_e: "Add Avenue" });
            this.avenues = l;
          })
          .catch((err) => {
            Swal.fire({
              icon: "error",
              title: `${this.$t("general.Error")}`,
              text: `${this.$t("general.Thereisanerrorinthesystem")}`,
            });
          });
      }
      if (id) {
        this.create.city_id = null;
        this.create.avenue_id = null;
        this.avenues = [];
        await adminApi
          .get(`/cities/get-drop-down?country_id=${id}`)
          .then((res) => {
            let l = res.data.data;
            l.unshift({ id: 0, name: "اضافة مدينة", name_e: "Add City" });
            this.cities = l;
          })
          .catch((err) => {
            Swal.fire({
              icon: "error",
              title: `${this.$t("general.Error")}`,
              text: `${this.$t("general.Thereisanerrorinthesystem")}`,
            });
          });
      }
    },
    async showAvenueModal(id = null, id2 = null) {
      if (this.create.avenue_id == 0) {
        this.$bvModal.show("avenue-create");
        this.create.avenue_id = null;
      }
    },
    getAvenue() {
      setTimeout(() => {
        let country = this.create.country_id;
        let city = this.create.city_id;
        adminApi
          .get(`/avenues/get-drop-down?country_id=${country}&city_id=${city}`)
          .then((res) => {
            let l = res.data.data;
            l.unshift({ id: 0, name: "اضافة منطقه", name_e: "Add Avenue" });
            this.avenues = l;
          })
          .catch((err) => {
            Swal.fire({
              icon: "error",
              title: `${this.$t("general.Error")}`,
              text: `${this.$t("general.Thereisanerrorinthesystem")}`,
            });
          });
      }, 500);
    },
    async getLocation() {
      if (navigator.geolocation) {
        return await navigator.geolocation.getCurrentPosition(
          this.showPosition
        );
      }
    },
    showPosition(position) {
      this.create.lat = position.coords.latitude;
      this.create.lng = position.coords.longitude;
    },
    arabicValue(txt) {
      this.create.name = arabicValue(txt);
    },
    arabicValueDescription(txt) {
      this.create.description = arabicValue(txt);
    },
    englishValue(txt) {
      this.create.name_e = englishValue(txt);
    },
    englishValueDescription(txt) {
      this.create.description_e = englishValue(txt);
    },
    /**
     *  image functions
     */
    b_onImageChanged(e) {
      const file = e.target.files[0];
      this.addImage(file);
    },
    addImage(file) {
      this.media = file; //upload
      if (file) {
        this.idDelete = null;
        let is_media = this.b_images.find(
          (e) => e.name == file.name.slice(0, file.name.indexOf("."))
        );
        this.idDelete = is_media ? is_media.id : null;
        if (!this.idDelete) {
          this.isLoader = true;
          let formDate = new FormData();
          formDate.append("media[0]", this.media);
          adminApi
            .post(`/media`, formDate)
            .then((res) => {
              let old_media = [];
              this.b_images.forEach((e) => old_media.push(e.id));
              let new_media = [];
              res.data.data.forEach((e) => new_media.push(e.id));
              adminApi
                .put(`/real-estate/buildings/${this.building_id}`, {
                  old_media,
                  media: new_media,
                })
                .then((res) => {
                  this.b_images = res.data.data.media ?? [];
                  if (this.b_images && this.b_images.length > 0) {
                    this.b_showPhoto =
                      this.b_images[this.b_images.length - 1].webp;
                  } else {
                    this.b_showPhoto = "../../../../../images/img-1.png";
                  }
                  this.$emit("getDataTable");
                })
                .catch((err) => {
                  Swal.fire({
                    icon: "error",
                    title: `${this.$t("general.Error")}`,
                    text: `${this.$t("general.Thereisanerrorinthesystem")}`,
                  });
                });
            })
            .catch((err) => {
              if (err.response.data) {
                this.errors = err.response.data.errors;
              } else {
                Swal.fire({
                  icon: "error",
                  title: `${this.$t("general.Error")}`,
                  text: `${this.$t("general.Thereisanerrorinthesystem")}`,
                });
              }
            })
            .finally(() => {
              this.isLoader = false;
            });
        } else {
          Swal.fire({
            title: `${this.$t("general.Thisfilehasalreadybeenuploaded")}`,
            type: "warning",
            showCancelButton: true,
            confirmButtonText: `${this.$t("general.Replace")}`,
            cancelButtonText: `${this.$t("general.Nocancel")}`,
            confirmButtonClass: "btn btn-success mt-2",
            cancelButtonClass: "btn btn-danger ml-2 mt-2",
            buttonsStyling: false,
          }).then((result) => {
            if (result.value) {
              this.isLoader = true;
              let formDate = new FormData();
              formDate.append("media[0]", this.media);
              adminApi
                .post(`/media`, formDate)
                .then((res) => {
                  let old_media = [];
                  this.b_images.forEach((e) => old_media.push(e.id));
                  old_media.splice(old_media.indexOf(this.idDelete), 1);
                  let new_media = [];
                  res.data.data.forEach((e) => new_media.push(e.id));

                  adminApi
                    .put(`/real-estate/buildings/${this.building_id}`, {
                      old_media,
                      media: new_media,
                    })
                    .then((res) => {
                      this.b_images = res.data.data.media ?? [];
                      if (this.b_images && this.b_images.length > 0) {
                        this.b_showPhoto =
                          this.b_images[this.b_images.length - 1].webp;
                      } else {
                        this.b_showPhoto = "../../../../../images/img-1.png";
                      }
                      this.$emit("getDataTable");
                    })
                    .catch((err) => {
                      Swal.fire({
                        icon: "error",
                        title: `${this.$t("general.Error")}`,
                        text: `${this.$t("general.Thereisanerrorinthesystem")}`,
                      });
                    });
                })
                .catch((err) => {
                  if (err.response.data) {
                    this.errors = err.response.data.errors;
                  } else {
                    Swal.fire({
                      icon: "error",
                      title: `${this.$t("general.Error")}`,
                      text: `${this.$t("general.Thereisanerrorinthesystem")}`,
                    });
                  }
                })
                .finally(() => {
                  this.isLoader = false;
                });
            }
          });
        }
      }
    },
    deleteImageCreate(id, index) {
      let old_media = [];
      this.b_images.forEach((e) => {
        if (e.id != id) {
          old_media.push(e.id);
        }
      });
      adminApi
        .put(`/real-estate/buildings/${this.building_id}`, { old_media })
        .then((res) => {
          this.builds[index] = res.data.data;
          this.b_images = res.data.data.media ?? [];
          if (this.b_images && this.b_images.length > 0) {
            this.b_showPhoto = this.b_images[this.b_images.length - 1].webp;
          } else {
            this.b_showPhoto = "../../../../../images/img-1.png";
          }
        })
        .catch((err) => {
          Swal.fire({
            icon: "error",
            title: `${this.$t("general.Error")}`,
            text: `${this.$t("general.Thereisanerrorinthesystem")}`,
          });
        });
    },
    b_changePhoto() {
      document.getElementById("b_uploadImageCreate").click();
    },
    /**
     *  video functions
     */
    AddVideo(action) {
      let data = action == "create" ? this.create : this.edit;
      this.$v.createVideo.$touch();
      if (this.$v.createVideo.$invalid) {
        return;
      } else {
        this.isLoader = true;
        this.errors = {};
        adminApi
          .put(`/real-estate/buildings/${this.building_id}`, {
            ...data,
            video_link: this.createVideo,
          })
          .then((res) => {
            this.$emit("getDataTable");
            this.frameUrl = res.data.data.video_link;
            setTimeout(() => {
              Swal.fire({
                icon: "success",
                text: `${this.$t("general.Editsuccessfully")}`,
                showConfirmButton: false,
                timer: 1500,
              });
            }, 500);
          })
          .catch((err) => {
            if (err.response.data) {
              this.errors = err.response.data.errors;
            } else {
              Swal.fire({
                icon: "error",
                title: `${this.$t("general.Error")}`,
                text: `${this.$t("general.Thereisanerrorinthesystem")}`,
              });
            }
          })
          .finally(() => {
            this.isLoader = false;
          });
      }
    },
    goToPropertyComponentTab() {
      // Programmatically switch to the "Property Components" tab
      this.$refs.propertyComponentsTab.activate();
    },
  },
};
</script>
<template>
  <!--  create   -->
  <div>
    <Country
      :companyKeys="companyKeys"
      :defaultsKeys="defaultsKeys"
      @created="getCategory"
      :isPage="false"
      :isPermission="isPermission"
      :id="'country-create'"
    />
    <City
      :companyKeys="companyKeys"
      :defaultsKeys="defaultsKeys"
      @created="getCity(create.country_id)"
      :isPage="false"
      :isPermission="isPermission"
      :id="'city-create'"
    />
    <Avenue
      :companyKeys="companyKeys"
      :defaultsKeys="defaultsKeys"
      @created="getAvenue"
      :isPage="false"
      :isPermission="isPermission"
      :id="'avenue-create'"
    />
    <b-modal
      :id="id"
      :title="
        type != 'edit'
          ? getCompanyKey('building_create_form')
          : getCompanyKey('building_edit_form')
      "
      title-class="font-18"
      dialog-class="modal-full-width"
      body-class="p-4 "
      :hide-footer="true"
      @show="resetModal"
      @hidden="resetModalHidden"
    >
      <form>
        <loader size="large" v-if="isCustom && !isPage" />
        <div class="mb-3 d-flex justify-content-end">
          <b-button
            v-if="type != 'edit'"
            variant="success"
            :disabled="!is_disabled"
            @click.prevent="resetForm"
            type="button"
            :class="['font-weight-bold px-2', is_disabled ? 'mx-2' : '']"
          >
            {{ $t("general.AddNewRecord") }}
          </b-button>
          <template v-if="!is_disabled">
            <b-button
              variant="success"
              type="submit"
              class="mx-1"
              v-if="!isLoader"
              @click.prevent="AddSubmit"
            >
              {{ type != "edit" ? $t("general.Add") : $t("general.edit") }}
            </b-button>
            <b-button variant="success" class="mx-1" disabled v-else>
              <b-spinner small></b-spinner>
              <span class="sr-only">{{ $t("login.Loading") }}...</span>
            </b-button>
          </template>
          <b-button
            @click.prevent="resetModalHidden"
            variant="danger"
            type="button"
          >
            {{ $t("general.Cancel") }}
          </b-button>
        </div>
        <b-tabs nav-class="nav-tabs nav-bordered">
          <b-tab :title="$t('general.DataEntry')" active>
            <div class="row">
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ getCompanyKey("building_name_ar") }}
                    <span class="text-danger">*</span>
                  </label>
                  <div dir="rtl">
                    <input
                      @keyup="arabicValue(create.name)"
                      type="text"
                      class="form-control"
                      v-model="$v.create.name.$model"
                      :class="{
                        'is-invalid': $v.create.name.$error || errors.name,
                        'is-valid': !$v.create.name.$invalid && !errors.name,
                      }"
                    />
                  </div>
                  <div
                    v-if="!$v.create.name.minLength"
                    class="invalid-feedback"
                  >
                    {{ $t("general.Itmustbeatleast") }}
                    {{ $v.create.name.$params.minLength.min }}
                    {{ $t("general.letters") }}
                  </div>
                  <div
                    v-if="!$v.create.name.maxLength"
                    class="invalid-feedback"
                  >
                    {{ $t("general.Itmustbeatmost") }}
                    {{ $v.create.name.$params.maxLength.max }}
                    {{ $t("general.letters") }}
                  </div>
                  <template v-if="errors.name">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.name"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ getCompanyKey("building_name_en") }}
                    <span class="text-danger">*</span>
                  </label>
                  <div dir="ltr">
                    <input
                      @keyup="englishValue(create.name_e)"
                      type="text"
                      class="form-control"
                      v-model="$v.create.name_e.$model"
                      :class="{
                        'is-invalid': $v.create.name_e.$error || errors.name_e,
                        'is-valid':
                          !$v.create.name_e.$invalid && !errors.name_e,
                      }"
                    />
                  </div>
                  <div
                    v-if="!$v.create.name_e.minLength"
                    class="invalid-feedback"
                  >
                    {{ $t("general.Itmustbeatleast") }}
                    {{ $v.create.name_e.$params.minLength.min }}
                    {{ $t("general.letters") }}
                  </div>
                  <div
                    v-if="!$v.create.name_e.maxLength"
                    class="invalid-feedback"
                  >
                    {{ $t("general.Itmustbeatmost") }}
                    {{ $v.create.name_e.$params.maxLength.max }}
                    {{ $t("general.letters") }}
                  </div>
                  <template v-if="errors.name_e">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.name_e"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
            </div>
            <hr
              style="
                margin: 10px 0 !important;
                border-top: 1px solid rgb(141 163 159 / 42%);
              "
            />
            <div class="row">
              <div class="col-md-4">
                <div class="form-group">
                  <label class="mr-2">
                    {{ $t("general.construction_year") }}
                    <span class="text-danger">*</span>
                  </label>
                  <select
                    class="custom-select mr-sm-2"
                    v-model="$v.create.construction_year.$model"
                    :class="{
                      'is-invalid':
                        $v.create.construction_year.$error ||
                        errors.construction_year,
                      'is-valid':
                        !$v.create.construction_year.$invalid &&
                        !errors.construction_year,
                    }"
                  >
                    <option value="" selected>
                      {{ $t("general.Choose") }}...
                    </option>
                    <option
                      v-for="year in getCurrentYear()"
                      v-if="year >= 2000"
                      :value="year"
                    >
                      {{ year }}
                    </option>
                  </select>
                  <template v-if="errors.construction_year">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.construction_year"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ getCompanyKey("building_area") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    step="0.1"
                    v-model="$v.create.building_area.$model"
                    :class="{
                      'is-invalid':
                        $v.create.building_area.$error || errors.building_area,
                      'is-valid':
                        !$v.create.building_area.$invalid &&
                        !errors.building_area,
                    }"
                  />
                  <template v-if="errors.building_area">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.building_area"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ getCompanyKey("building_land_area") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    step="0.1"
                    v-model="$v.create.land_area.$model"
                    :class="{
                      'is-invalid':
                        $v.create.land_area.$error || errors.land_area,
                      'is-valid':
                        !$v.create.land_area.$invalid && !errors.land_area,
                    }"
                  />
                  <template v-if="errors.land_area">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.land_area"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
            </div>
            <hr
              style="
                margin: 10px 0 !important;
                border-top: 1px solid rgb(141 163 159 / 42%);
              "
            />
            <div class="row">
              <div class="col-md-4">
                <div class="form-group position-relative">
                  <label class="control-label">
                    {{ getCompanyKey("country") }}
                    <span class="text-danger">*</span>
                  </label>
                  <multiselect
                    @input="showCountryModal"
                    v-model="$v.create.country_id.$model"
                    :options="countries.map((type) => type.id)"
                    :custom-label="
                      (opt) =>
                        countries.find((x) => x.id == opt)
                          ? countries.find((x) => x.id == opt).name
                          : ''
                    "
                  >
                  </multiselect>
                  <div
                    v-if="$v.create.country_id.$error || errors.country_id"
                    class="text-danger"
                  >
                    {{ $t("general.fieldIsRequired") }}
                  </div>
                  <template v-if="errors.country_id">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.country_id"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group position-relative">
                  <label class="control-label">
                    {{ getCompanyKey("city") }}
                    <span class="text-danger">*</span>
                  </label>
                  <multiselect
                    @input="getCity()"
                    @change="getAvenue"
                    v-model="$v.create.city_id.$model"
                    :options="cities.map((type) => type.id)"
                    :custom-label="
                      (opt) =>
                        cities.find((x) => x.id == opt)
                          ? cities.find((x) => x.id == opt).name
                          : ''
                    "
                  >
                  </multiselect>
                  <div
                    v-if="$v.create.city_id.$error || errors.city_id"
                    class="text-danger"
                  >
                    {{ $t("general.fieldIsRequired") }}
                  </div>
                  <template v-if="errors.city_id">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.city_id"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group position-relative">
                  <label class="control-label">
                    {{ getCompanyKey("avenue") }}
                    <span class="text-danger">*</span>
                  </label>
                  <multiselect
                    @input="showAvenueModal"
                    v-model="$v.create.avenue_id.$model"
                    :options="avenues.map((type) => type.id)"
                    :custom-label="
                      (opt) =>
                        avenues.find((x) => x.id == opt)
                          ? avenues.find((x) => x.id == opt).name
                          : ''
                    "
                  >
                  </multiselect>
                  <div
                    v-if="$v.create.avenue_id.$error || errors.avenue_id"
                    class="text-danger"
                  >
                    {{ $t("general.fieldIsRequired") }}
                  </div>
                  <template v-if="errors.city_id">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.avenue_id"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ getCompanyKey("building_longitude") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    step="0.00000000000001"
                    v-model="$v.create.lng.$model"
                    :class="{
                      'is-invalid': $v.create.lng.$error || errors.lng,
                      'is-valid': !$v.create.lng.$invalid && !errors.lng,
                    }"
                  />
                  <template v-if="errors.lng">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.lng"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ getCompanyKey("building_latitude") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    data-create="9"
                    step="0.00000000000001"
                    v-model="$v.create.lat.$model"
                    :class="{
                      'is-invalid': $v.create.lat.$error || errors.lat,
                      'is-valid': !$v.create.lat.$invalid && !errors.lat,
                    }"
                  />
                  <template v-if="errors.lat">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.lat"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
              <div class="col-md-4 d-flex align-items-center mt-3">
                <button
                  class="btn btn-primary"
                  type="button"
                  @click="getLocation"
                >
                  {{ $t("general.location") }}
                </button>
              </div>
            </div>
            <hr
              style="
                margin: 10px 0 !important;
                border-top: 1px solid rgb(141 163 159 / 42%);
              "
            />
            <div class="row">
              <div class="col-md-6">
                <div class="form-group">
                  <label class="mr-2">
                    {{ getCompanyKey("building_description_ar") }}
                    <span class="text-danger">*</span>
                  </label>
                  <textarea
                    @input="arabicValueDescription(create.description)"
                    v-model="$v.create.description.$model"
                    class="form-control"
                    :maxlength="1000"
                    rows="5"
                  ></textarea>
                  <template v-if="errors.description">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.description"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
              <div class="col-md-6">
                <div class="form-group">
                  <label class="mr-2">
                    {{ getCompanyKey("building_description_en") }}
                    <span class="text-danger">*</span>
                  </label>
                  <textarea
                    @input="arabicValueDescription(create.description_e)"
                    v-model="$v.create.description_e.$model"
                    class="form-control"
                    :maxlength="1000"
                    rows="5"
                  ></textarea>
                  <template v-if="errors.description_e">
                    <ErrorMessage
                      v-for="(errorMessage, index) in errors.description_e"
                      :key="index"
                      >{{ errorMessage }}</ErrorMessage
                    >
                  </template>
                </div>
              </div>
            </div>
            <div class="d-flex justify-content-end mt-4">
              <b-button
                variant="primary"
                @click.prevent="goToPropertyComponentTab"
              >
                {{ $t("general.Next") }}
              </b-button>
            </div>
          </b-tab>
          <b-tab
            :title="$t('general.Property Components')"
            ref="propertyComponentsTab"
          >
            <div class="row">
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.floors_number") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.floors_number.$model"
                    :class="{
                      'is-invalid':
                        $v.create.floors_number.$error || errors.floors_number,
                      'is-valid':
                        !$v.create.floors_number.$invalid &&
                        !errors.floors_number,
                    }"
                  />
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.mediums_number") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.mediums_number.$model"
                    :class="{
                      'is-invalid':
                        $v.create.mediums_number.$error ||
                        errors.mediums_number,
                      'is-valid':
                        !$v.create.mediums_number.$invalid &&
                        !errors.mediums_number,
                    }"
                  />
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.ground_floors_number") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.ground_floors_number.$model"
                    :class="{
                      'is-invalid':
                        $v.create.ground_floors_number.$error ||
                        errors.ground_floors_number,
                      'is-valid':
                        !$v.create.ground_floors_number.$invalid &&
                        !errors.ground_floors_number,
                    }"
                  />
                </div>
              </div>
            </div>
            <hr
              style="
                margin: 10px 0 !important;
                border-top: 1px solid rgb(141 163 159 / 42%);
              "
            />
            <div class="row">
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.vaults_number") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.vaults_number.$model"
                    :class="{
                      'is-invalid':
                        $v.create.vaults_number.$error || errors.vaults_number,
                      'is-valid':
                        !$v.create.vaults_number.$invalid &&
                        !errors.vaults_number,
                    }"
                  />
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.central_air_conditioning") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.central_air_conditioning.$model"
                    :class="{
                      'is-invalid':
                        $v.create.central_air_conditioning.$error ||
                        errors.central_air_conditioning,
                      'is-valid':
                        !$v.create.central_air_conditioning.$invalid &&
                        !errors.central_air_conditioning,
                    }"
                  />
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.elevators_number") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.elevators_number.$model"
                    :class="{
                      'is-invalid':
                        $v.create.elevators_number.$error ||
                        errors.elevators_number,
                      'is-valid':
                        !$v.create.elevators_number.$invalid &&
                        !errors.elevators_number,
                    }"
                  />
                </div>
              </div>
            </div>
            <hr
              style="
                margin: 10px 0 !important;
                border-top: 1px solid rgb(141 163 159 / 42%);
              "
            />
            <div class="row">
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.electricity_meters_number") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.electricity_meters_number.$model"
                    :class="{
                      'is-invalid':
                        $v.create.electricity_meters_number.$error ||
                        errors.electricity_meters_number,
                      'is-valid':
                        !$v.create.electricity_meters_number.$invalid &&
                        !errors.electricity_meters_number,
                    }"
                  />
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.water_meters_number") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.water_meters_number.$model"
                    :class="{
                      'is-invalid':
                        $v.create.water_meters_number.$error ||
                        errors.water_meters_number,
                      'is-valid':
                        !$v.create.water_meters_number.$invalid &&
                        !errors.water_meters_number,
                    }"
                  />
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.gas_meters_number") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.gas_meters_number.$model"
                    :class="{
                      'is-invalid':
                        $v.create.gas_meters_number.$error ||
                        errors.gas_meters_number,
                      'is-valid':
                        !$v.create.gas_meters_number.$invalid &&
                        !errors.gas_meters_number,
                    }"
                  />
                </div>
              </div>
            </div>
          </b-tab>
          <b-tab :title="$t('Financials Details')" ref="Financials DetailsTab">
            <div class="row">
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.buying_price") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.buying_price.$model"
                    :class="{
                      'is-invalid':
                        $v.create.buying_price.$error || errors.buying_price,
                      'is-valid':
                        !$v.create.buying_price.$invalid &&
                        !errors.buying_price,
                    }"
                  />
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.buying_date") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="date"
                    class="form-control"
                    v-model="$v.create.buying_date.$model"
                    :class="{
                      'is-invalid':
                        $v.create.buying_date.$error || errors.buying_date,
                      'is-valid':
                        !$v.create.buying_date.$invalid && !errors.buying_date,
                    }"
                  />
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.middleman_cost") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.middleman_cost.$model"
                    :class="{
                      'is-invalid':
                        $v.create.middleman_cost.$error ||
                        errors.middleman_cost,
                      'is-valid':
                        !$v.create.middleman_cost.$invalid &&
                        !errors.middleman_cost,
                    }"
                  />
                </div>
              </div>
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.registration_cost") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    class="form-control"
                    v-model="$v.create.registration_cost.$model"
                    :class="{
                      'is-invalid':
                        $v.create.registration_cost.$error ||
                        errors.registration_cost,
                      'is-valid':
                        !$v.create.registration_cost.$invalid &&
                        !errors.registration_cost,
                    }"
                  />
                </div>
              </div>
            </div>
          </b-tab>

          <b-tab :title="$t('general.Management Policy')">
            <div class="row">
              <div class="col-md-6">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.CalculationType") }}
                  </label>
                  <b-dropdown v-model="selectedCalculationType">
                    <b-dropdown-item
                      v-for="calculationType in calculationTypes"
                      :key="calculationType.id"
                      @click="selectCalculationType(calculationType)"
                    >
                      {{ calculationType.label }}
                    </b-dropdown-item>
                  </b-dropdown>
                </div>
              </div>
              <!-- Add more form fields related to the selected calculation type here -->
            </div>
          </b-tab>

          <b-tab :disabled="!building_id" :title="$t('general.ImageUploads')">
            <div class="row">
              <input
                accept="image/png, image/gif, image/jpeg, image/jpg"
                type="file"
                id="b_uploadImageCreate"
                @change.prevent="b_onImageChanged"
                class="input-file-upload position-absolute"
                :class="[
                  'd-none',
                  {
                    'is-invalid': $v.create.media.$error || errors.media,
                    'is-valid': !$v.create.media.$invalid && !errors.media,
                  },
                ]"
              />
              <div class="col-md-8 my-1">
                <!-- file upload -->
                <div
                  class="row align-content-between"
                  style="width: 100%; height: 100%"
                >
                  <div class="col-12">
                    <div class="d-flex flex-wrap">
                      <div
                        :class="[
                          'dropzone-previews col-4 position-relative mb-2',
                        ]"
                        v-for="(photo, index) in b_images"
                        :key="photo.id"
                      >
                        <div
                          :class="[
                            'card mb-0 shadow-none border',
                            b_images.length - 1 == index ? 'bg-primary' : '',
                          ]"
                        >
                          <div class="p-2">
                            <div class="row align-items-center">
                              <div
                                class="col-auto"
                                @click="b_showPhoto = photo.webp"
                              >
                                <img
                                  data-dz-thumbnail
                                  :src="photo.webp"
                                  class="avatar-sm rounded bg-light"
                                  @error="
                                    src = '../../../../../images/img-1.png'
                                  "
                                />
                              </div>
                              <div class="col pl-0">
                                <a
                                  href="javascript:void(0);"
                                  :class="[
                                    'font-weight-bold',
                                    b_images.length - 1 == index
                                      ? 'text-white'
                                      : 'text-muted',
                                  ]"
                                  data-dz-name
                                >
                                  {{ photo.name }}
                                </a>
                              </div>
                              <!-- Button -->
                              <a
                                href="javascript:void(0);"
                                :class="[
                                  'btn-danger dropzone-close',
                                  $i18n.locale == 'ar'
                                    ? 'dropzone-close-rtl'
                                    : '',
                                ]"
                                data-dz-remove
                                @click.prevent="
                                  deleteImageCreate(photo.id, index)
                                "
                              >
                                <i class="fe-x"></i>
                              </a>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="footer-image col-12">
                    <b-button
                      @click="b_changePhoto"
                      variant="success"
                      type="button"
                      class="mx-1 font-weight-bold px-3"
                      v-if="!isLoader"
                    >
                      {{ $t("general.Add") }}
                    </b-button>
                    <b-button variant="success" class="mx-1" disabled v-else>
                      <b-spinner small></b-spinner>
                      <span class="sr-only">{{ $t("login.Loading") }}...</span>
                    </b-button>
                  </div>
                </div>
              </div>
              <div class="col-md-4">
                <div class="show-dropzone">
                  <img
                    :src="b_showPhoto"
                    class="img-thumbnail"
                    @error="src = '../../../../../images/img-1.png'"
                  />
                </div>
              </div>
            </div>
          </b-tab>
          <b-tab :disabled="!building_id" :title="$t('general.video')">
            <div class="d-flex justify-content-end">
              <b-button
                variant="success"
                type="button"
                class="mx-1"
                v-if="!isLoader"
                @click.prevent="AddVideo('create')"
              >
                {{ $t("general.Add") }}
              </b-button>
              <b-button variant="success" class="mx-1" disabled v-else>
                <b-spinner small></b-spinner>
                <span class="sr-only">{{ $t("login.Loading") }}...</span>
              </b-button>
            </div>
            <div class="row">
              <div class="col-md-4">
                <div class="form-group">
                  <label class="control-label">
                    {{ $t("general.video") }}
                    <span class="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="$v.createVideo.$model"
                    :class="{
                      'is-invalid': $v.createVideo.$error,
                      'is-valid': !$v.createVideo.$invalid,
                    }"
                  />
                </div>
              </div>
              <div v-html="frameUrl" v-if="frameUrl" class="col-md-12"></div>
            </div>
          </b-tab>
        </b-tabs>
      </form>
    </b-modal>
  </div>
  <!--  /create   -->
</template>
<style scoped>
.dropdown-menu-custom-company.dropdown .dropdown-menu {
  padding: 5px 10px !important;
  overflow-y: scroll;
  height: 300px;
}
</style>
