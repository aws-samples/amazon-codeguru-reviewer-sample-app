<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('rlst_buildings', function (Blueprint $table) {
            $table->id();
            $table->double('name', 100);
            $table->double('name_e', 100);
            $table->double('description')->nullable();
            $table->double('description_e')->nullable();
            $table->double('land_area')->nullable()->default(0);
            $table->double('building_area')->nullable()->default(0)->comment("Building area in Square Meter (<=land area)");
            $table->char('construction_year', 4)->nullable()->default('2018');
            $table->unsignedInteger('project_id')->nullable()->default(0);
            $table->unsignedInteger('country_id')->nullable()->default(0);
            $table->unsignedInteger('city_id')->nullable()->default(0);
            $table->unsignedInteger('avenue_id')->nullable()->default(0);
            $table->unsignedInteger('street_id')->nullable()->default(0);
            $table->double('lng')->nullable()->default(0);
            $table->double('lat')->nullable()->default(0);
            $table->text('properties')->nullable();
            $table->text('attachments')->nullable();
            $table->string("module")->nullable();
            $table->softDeletes();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('rlst_buildings');
    }
};